package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CIMainActivity extends Activity {
//	private Button buttonGo;
	private TextView introduction;
	private TextView textViewMethod1;
	private TextView textViewMethod2;
	private TextView textViewTime;
	
	private SharedPreferences prefs;
	private Handler mainHandler;
	private Runnable runnableCompare;
	private Runnable runnableResult;
	
	private int imageRqCode1 = 1;
	private int imageRqCode2 = 2;
	private boolean isLoadedImg1 = false;
	private boolean isLoadedImg2 = false;
	private long timeCompareSimple; //for both compareSimple and compareSimpleAVG
	private long timeVectorDiff;

	private int getResult1=0;
	private int getResult2=0;
	public static int fflag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compare_image);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		buttonGo = (Button) findViewById(R.id.buttonGo);
		textViewMethod1 = (TextView) findViewById(R.id.textViewMethod1);
		textViewMethod2 = (TextView) findViewById(R.id.textViewMethod2);
		textViewTime = (TextView) findViewById(R.id.textViewTime);
		introduction=(TextView)findViewById(R.id.introduction);
		introduction.setText("Oh, honey! Try to open your eyes and focus^v^\n " +
				//"If the average comparison result of two algorithms is less than 90%, that means your mind is not REFRESHED!\nYou can do this!");
				"The alarm can only be turned off by having the average comparison results of two algorithms less than 90%.\nYou can do this!");
		
		mainHandler = new Handler();
		runnableCompare = new Runnable() {
			public void run() {
				if(isLoadedImg1 && isLoadedImg2) {
					long start = System.currentTimeMillis();
				
					CIComparator imageComp = new CIComparator();
					imageComp.setImages(prefs.getString(CIConstants.PREF_IMAGE1, ""), prefs.getString(CIConstants.PREF_IMAGE2, ""), prefs.getInt(CIConstants.PREF_SAMPLE_RATE1, 0), prefs.getInt(CIConstants.PREF_SAMPLE_RATE2, 0));
					Log.d("Testing", prefs.getString(CIConstants.PREF_IMAGE1, ""));
					Log.d("Testing", prefs.getString(CIConstants.PREF_IMAGE2, ""));

					getResult1=imageComp.compareSimple();
					timeCompareSimple = System.currentTimeMillis() - start;
					start = System.currentTimeMillis();
					getResult2=imageComp.compareVectorDiffAVG();
					timeVectorDiff = System.currentTimeMillis() - start;
					
					mainHandler.post(runnableResult);
				} else {
					mainHandler.post(runnableResult);
				}
			}
		};
		runnableResult = new Runnable() {
			public void run() {
				if(isLoadedImg1 && isLoadedImg2) {
					textViewTime.setText("Algorithm 1:\t\t" + timeCompareSimple + " ms" + "\n" + "Algorithm 2:\t\t" + timeVectorDiff + "ms");
					textViewMethod1.setText(CIConstants.RESULT_COMPARESIMPLE);
					textViewMethod2.setText(CIConstants.RESULT_COMPAREVECTORDIFF);
					//showToast("Sorry, you need to try again.");

					if((getResult1+getResult2)/2>=90){
						/**
						 * * chen_start
						 */

//						MainActivity.Flag=1;
//						Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
//						PendingIntent pintent = PendingIntent.getService(CIMainActivity.this, 0, intent, 0);
//						AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//						stopService(intent);
//						alarm.cancel(pintent);
						fflag=1;
//						Intent intent=new Intent(CIMainActivity.this,CameraActivity.class);
//						startActivity(intent);
						finish();

						/**
						 * * chen_end
						 */
					}else {
						showToast("Mission Failed-_-! Please click on back button and try again.");
					}
				} else {
					Toast.makeText(CIMainActivity.this, getResources().getString(R.string.toast_images_notloaded), Toast.LENGTH_LONG).show();
				}
			}
		};
		
		
//		buttonImage1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(Intent.ACTION_PICK, Uri.parse("image/*"));
//				i.setType("image/*");
//				startActivityForResult(i, imageRqCode1);
//			}
//		});
//
//		buttonImage2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(Intent.ACTION_PICK, Uri.parse("image/*"));
//				i.setType("image/*");
//				startActivityForResult(i, imageRqCode2);
//			}
//		});
		
//		buttonGo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				sampleProcess(imageRqCode1);
//				sampleProcess(imageRqCode2);
//				Thread threadCompare = new Thread(runnableCompare);
//				threadCompare.start();
//			}
//		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		sampleProcess(imageRqCode1);
		sampleProcess(imageRqCode2);
		Thread threadCompare = new Thread(runnableCompare);
		threadCompare.start();

	}

	//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.cimain, menu);
//		return true;
//	}

	public void sampleProcess(int requestCode){
		if(requestCode == imageRqCode1) {
			String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/preset.jpg";
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Pictures/preset.jpg");
			//get the size of the image in bytes (long)
			long fileSize = 0;
			fileSize = file.length();
			Log.d("length",String.valueOf(fileSize));

			//set sample rate according to image size
			int sampleRate = 0;
			if(fileSize < 512000) {
				sampleRate = 4;
			} else if(fileSize >= 512000 & fileSize < 1024000) {
				sampleRate = 8;
			} else if(fileSize >= 1024000) {
				sampleRate = 16;
			}
			Log.d("SampleRate", String.valueOf(sampleRate));
			prefs.edit().putInt(CIConstants.PREF_SAMPLE_RATE1, sampleRate).commit();
			prefs.edit().putString(CIConstants.PREF_IMAGE1, filePath).commit();
			isLoadedImg1 = true;
		}

		if(requestCode == imageRqCode2) {
			String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Pictures/pic.jpg";
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Pictures/pic.jpg");
			//get the size of the image in bytes (long)
			long fileSize = 0;
			fileSize = file.length();
			Log.d("length",String.valueOf(fileSize));

			//set sample rate according to image size
			int sampleRate = 0;
			if(fileSize < 512000) {
				sampleRate = 4;
			} else if(fileSize >= 512000 & fileSize < 1024000) {
				sampleRate = 8;
			} else if(fileSize >= 1024000) {
				sampleRate = 16;
			}
			Log.d("SampleRate", String.valueOf(sampleRate));
			prefs.edit().putInt(CIConstants.PREF_SAMPLE_RATE2, sampleRate).commit();
			prefs.edit().putString(CIConstants.PREF_IMAGE2, filePath).commit();
			isLoadedImg2 = true;
		}

	}

	private void showToast(final String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

}
