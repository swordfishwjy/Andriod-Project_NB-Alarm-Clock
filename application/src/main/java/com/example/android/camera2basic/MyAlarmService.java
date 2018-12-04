package com.example.android.camera2basic;

/**
 * Created by aaaaaqi5 on 11/24/2015.
 */

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MyAlarmService extends Service {
    private MediaPlayer mMediaPlayer;
    private TimerTask  task=null;
    private Timer timer=null;
    public static Vibrator mvibrator;
    private SQLiteDatabase db2;

//    private int volumeCounter=0;


    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
//        isRunning(getApplicationContext());
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int x=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        for(int i=x;i>=5;i--) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        super.onCreate();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);
        //设置可以重复播放
        mMediaPlayer.setLooping(true);
        mvibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    //    long[] pattern = {0, 1000, 1000};
    //    mvibrator.vibrate(pattern, 0);
        Log.i("SERVICE","ONCREATE");
        

    }

/****************改!!加入了铃声渐强功能*******/
    @Override
    public int onStartCommand(Intent intent, int flag,int startId)
    {
        super.onStartCommand(intent, flag, startId);
        Log.i("activeas",String.valueOf(MainActivity.active));
        new Thread(new Runnable() {
            @Override
            public void run() {
              //  playSound(MyAlarmService.this, getAlarmUri());
             //   Intent intent1 = new Intent(,MainActivity.class);
              //  intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //    mMediaPlayer.start();
                mvibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {0, 1000, 1000};
                mvibrator.vibrate(pattern, 0);

                timer=new Timer();
                task=new TimerTask(){
                    @Override
                    public void run() {
                        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                        int x=am.getStreamVolume(AudioManager.STREAM_MUSIC);
//                        Log.d("volume", String.valueOf(x));
//                        volumeCounter=x;
                        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

                        if(MainActivity.Flag == 1){
                            mMediaPlayer.stop();
                            mvibrator.cancel();
                            stopSelf();
                            timer.cancel();
                        }
                    }

                };
                timer.schedule(task, 400,400);


               // Toast.makeText(this, "ServiceClass.onStart()", Toast.LENGTH_LONG).show();
                Log.d("Testing", "Service got started");
            }
        }).run();

        /**
         * * chen_start
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent i;
                int flag = 0;
                db2 = openOrCreateDatabase("flag.db", Context.MODE_PRIVATE, null);
                Cursor c = db2.query("flag",null,null,null,null,null,null);
                if(c.getCount()!=0) {
                    if (c.moveToFirst()) {//check is null or not
                        flag = c.getInt(c.getColumnIndex("f"));
                        Log.i("flagg",String.valueOf(flag));
                    }
                    c.close();
                }
                db2.close();
                switch (flag) {
                    case 1:
                        i = new Intent(getApplicationContext(), StepDetctor.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    case 2:
                        i = new Intent(getApplicationContext(), CameraActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    case 3:
                        i = new Intent(getApplicationContext(), gesture.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    case 4:
                        i = new Intent(getApplicationContext(), shake.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    case 5:
                        i = new Intent(getApplicationContext(), shake.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    case 6:
                        i=new Intent(getApplicationContext(),VoiceDetector.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
        }

            }
        }).run();
        /**
         * * chen_end
         */

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
