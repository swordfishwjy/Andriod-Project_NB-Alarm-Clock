

package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CameraActivity extends Activity {
//    private Button button;
    private Button preset;
    private Button compare;
    private ImageView imageView;
    //RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
//        button=(Button)findViewById(R.id.addView);
//        preset=(Button)findViewById(R.id.preset);
        compare=(Button)findViewById(R.id.compare);
        imageView=(ImageView)findViewById(R.id.picutre);
        //relativeLayout=(RelativeLayout)findViewById(R.id.activityCameraLayout);


        //设置透明度
        imageView.setImageAlpha(160);
            File isFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Pictures/preset.jpg");
        if(isFile.exists()) {
            try{
//                    File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/", "preset.jpg");
                File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Pictures/preset.jpg");
                FileInputStream image=new FileInputStream(mFile);
                Bitmap bitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeStream(image),360,450,true);
                imageView.setImageBitmap(bitmap);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
            if (null == savedInstanceState) {
                Camera2BasicFragment f=new Camera2BasicFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container1, f)
                        .commit();
            }
        }else{
            showToast("Please set a picture first!");
        }

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
////                    File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/", "preset.jpg");
//                    File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Pictures/preset.jpg");
//                    FileInputStream image=new FileInputStream(mFile);
//                    Bitmap bitmap= BitmapFactory.decodeStream(image);
//                    imageView.setImageBitmap(bitmap);
//                }catch(FileNotFoundException e){
//                    e.printStackTrace();
//                }
//            }
//        });


        // 设置对比的背景照片
//        preset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView.setVisibility(View.INVISIBLE);
//                Camera2BasicFragment_preset f=new Camera2BasicFragment_preset();
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.container1, f)
//                        .commit();
//            }
//        });

        //开始对比图片相似度
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CameraActivity.this,CIMainActivity.class);
                startActivity(intent);
            }
        });




    }


    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        CIMainActivity.fflag=0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("wangjianyu", String.valueOf(CIMainActivity.fflag));

        // decide whether the image is compared successfully

        if(CIMainActivity.fflag==1){
            MainActivity.Flag=1;
        /*    Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
            PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            stopService(intent);
            alarm.cancel(pintent);*/

            finish();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
