package com.example.android.camera2basic;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView setTimeHistoryTV;
	TextView hintTv;
	TextView setTime1;
	Button mButton1;
	Button mButton2;
	String time1String = null;
	String defalutString = "No Alarm";

	AlertDialog builder = null;
	Calendar c = Calendar.getInstance();

	/**
	 * * chen_start
	 */
	private static Context context;
	private ArrayList<PopupView> popupViews;
	private PopupListView popupListView;
	String[] modeList = new String[]{"Let System Decide", "Crazy Jump", "Take A Picture",
			"Draw With Fingers", "Crazy Shake", "Draw In The Air", "Scream"};
	private int p;
	private Switch turnOnOffTBT;
	//private ImageButton trashcanBT;
	private final String TAG = "MainActivity";

	public static Context getAppContext(){
		return MainActivity.context;
	}

	public static int Flag=0;
	public static int function_flag=0;
	public static boolean active=true;


	private SQLiteDatabase db2;


	/**
	 * * chen_end
	 */

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_start_screen);

		initialList(); //Xu
//		/**
//		 * * * chen_start
//		 */
//		context=getApplicationContext();
//
//		/**
//		 * * chen_end
//		 */
//		//取得活动的Preferences对象
//		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
//		time1String = settings.getString("TIME1", defalutString);
//
//		setTime1.setText(time1String);
	//	active=true;
		Flag = 0;
		//show history clock setting_time,if no one found--default value will be shown

		setTimeHistoryTV = (TextView) findViewById(R.id.setTimeHistory);
		//trun on or off last alarm
		turnOnOffTBT = (Switch) findViewById(R.id.trunONOFF);
		turnOnOffTBT.setChecked(false);
		turnOnOffTBT.setTrackDrawable(new SwitchTrackTextDrawable(this, "OFF", "ON"));
		turnOnOffTBT.getBackground().setAlpha(100);
		popupListView.setVisibility(View.INVISIBLE);
		hintTv=(TextView)findViewById(R.id.hint);


		//delete previous alarm
		//trashcanBT = (ImageButton) findViewById(R.id.deleteBT);
		//setting button
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		time1String = settings.getString("TIME1", defalutString);
		Log.i(TAG, time1String);
		Log.i(TAG, setTimeHistoryTV.getText().toString());
		Log.i(TAG, String.valueOf(time1String == "NoOne"));
		if (time1String == defalutString) {

			setTimeHistoryTV.setText(defalutString);
			//trashcanBT.setVisibility(View.INVISIBLE);
			//turnOnOffTBT.setVisibility(View.INVISIBLE);
		} else {
			//trashcanBT.setVisibility(View.VISIBLE);
			//turnOnOffTBT.setVisibility(View.VISIBLE);
			setTimeHistoryTV.setText("ALARM\n"+time1String);
		}

		try {
			context = getApplicationContext();
		} catch (NullPointerException e) {
			Log.e("error", e.toString());
		}

		turnofoffbt();
		//deletebt();
	}

	private void initialList() {
		//mode listView
		popupViews = new ArrayList<>();
		popupListView = (PopupListView) findViewById(R.id.popupListView);
		for (int i = 0; i < 7; i++) {
			p = i;
			final PopupView popupView = new PopupView(this, R.layout.popup_view_item) {
				@Override
				public void setViewsElements(View view) {
					TextView textView = (TextView) view.findViewById(R.id.title);
					textView.getBackground().setAlpha(80);
					textView.setText(modeList[p]);
				}
			};
			popupViews.add(popupView);
		}
		popupListView.init(null);
		popupListView.setItemViews(popupViews);

		popupListView.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
					case 0:
						MainActivity.function_flag = (int) (Math.random()*5 + 1);
						if(MainActivity.function_flag == 2){
							Intent i = new Intent(MainActivity.this, CameraActivity_preset.class);
							startActivity(i);
						}
						break;
					case 1:
						MainActivity.function_flag = 1;
						break;
					case 2:
						MainActivity.function_flag = 2;
						Intent i = new Intent(MainActivity.this, CameraActivity_preset.class);
						startActivity(i);
						break;
					case 3:
						MainActivity.function_flag = 3;
						break;
					case 4:
						MainActivity.function_flag = 4;
						break;
					case 5:
						MainActivity.function_flag = 5;
						break;
					case 6:
						MainActivity.function_flag = 6;
						break;
				}
				db2 = openOrCreateDatabase("flag.db", Context.MODE_PRIVATE, null);
				db2.execSQL("Drop Table if exists flag");
				db2.execSQL("Create Table if not exists flag (f int not null)");
				db2.execSQL("INSERT INTO flag VALUES (?)", new Object[]{function_flag});
				db2.close();
				addclockbt();
				//Log.d(TAG, position + " function_flag");
			}
		});
	}
	public void addclockbt() {
		c.setTimeInMillis(System.currentTimeMillis());
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);

		new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int hourOfDay,
								  int minute) {
				c.setTimeInMillis(System.currentTimeMillis());
				c.set(Calendar.HOUR_OF_DAY, hourOfDay);
				c.set(Calendar.MINUTE, minute);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);

				//Log.d(TAG, "start run alarm activity");
				Intent intent = new Intent(MainActivity.this, CallAlarm.class);
				PendingIntent sender = PendingIntent.getBroadcast(
						MainActivity.this, 0, intent, 0);
				AlarmManager am;
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				//if(active){

					Log.i(TAG+"panduan",String.valueOf(active));
					am.set(AlarmManager.RTC_WAKEUP,
							c.getTimeInMillis(),
							sender
					);
			//	}

				String tmpS = format(hourOfDay) + "：" + format(minute);
				setTimeHistoryTV.setText(tmpS);

				//SharedPreferences保存数据，并提交
				SharedPreferences time1Share = getPreferences(0);
				SharedPreferences.Editor editor = time1Share.edit();
				editor.putString("TIME1", tmpS);
				editor.commit();

				Toast.makeText(MainActivity.this, "I'll wake you up at " + tmpS,
						Toast.LENGTH_SHORT)
						.show();
			}
		}, mHour, mMinute, true).show();
	}
/*
	public void deletebt() {
		trashcanBT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				setTimeHistoryTV.setText(defalutString);
				SharedPreferences time1Share = getPreferences(0);
				SharedPreferences.Editor editor = time1Share.edit();
				editor.putString("TIME1", defalutString);
				editor.commit();
			//	trashcanBT.setVisibility(View.INVISIBLE);
				active=false;
				Log.i("active",String.valueOf(active));
				//turnOnOffTBT.setVisibility(View.INVISIBLE);
			}
		});
	}
*/
	public void turnofoffbt() {
		turnOnOffTBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//active=true;
					popupListView.setVisibility(View.VISIBLE);
					hintTv.setText("Choose The Way You Want To Wake Up:");

					Log.i(TAG + "switch", String.valueOf(active));
				} else {
					popupListView.setVisibility(View.INVISIBLE);
					hintTv.setText("Click Switch To Set A New Alarm");
				}
				//active=false;

				Log.i(TAG + "switch", String.valueOf(active));
			}
		});
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if(keyCode == KeyEvent.KEYCODE_BACK){
			builder = new AlertDialog.Builder(MainActivity.this)
					.setIcon(R.drawable.clock)
					.setTitle("Oh, honey!")
					.setMessage("You sure you wanna shut me down?")
					.setPositiveButton("Pretty Sure!",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int whichButton) {
									MainActivity.this.finish();
								}
							})
					.setNegativeButton("No, I want you back!",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int whichButton) {
									builder.dismiss();
								}
							}).show();
		}
		return true;
	}

	private String format(int x)
	{
		String s=""+x;
		if(s.length()==1) s="0"+s;
		return s;
	}

	/**
	 * * chen_start
	 */

	@Override
	public void onDestroy(){
		super.onDestroy();
		Flag=0;
	}


	@Override
	public void onStart(){
		super.onStart();
		//Log.i(TAG, "onStart");

		Log.i("flag", String.valueOf(Flag));
		if(Flag==1){

			Flag=0;
			finish();
			System.exit(0);
		}

	}

	/**
	 * * chen_end
	 */
}