package com.example.android.camera2basic;

/**
 * Created by swordfish on 11/27/15.
 */

import java.io.File;
import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceDetector extends Activity{
    private MediaRecorder mp;
    private TextView txt1;
    private double value=0;
    private TimerTask  task=null;
    private Timer timer=null;
    private double maxx=0;
    private TextView maxtext;
    private TextView completeTimes;
    private int times=3;
    private int countTimes=0;
    File soundFile=null;
   // private ImageButton startbt;
   private Button startbt;
//    private Button endbt;
   // private TextView introduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        int x=am.getStreamVolume(AudioManager.STREAM_MUSIC);
//        Log.d("volume", String.valueOf(x));
//        for(int i=x;i>=5;i--) {
//            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_layout);
        txt1=(TextView) findViewById(R.id.voice);
        maxtext=(TextView)findViewById(R.id.maxVoice);
        completeTimes=(TextView)findViewById(R.id.completeTimes);
        startbt=(Button)findViewById(R.id.start);
//        endbt=(Button)findViewById(R.id.end);
       // introduction=(TextView)findViewById(R.id.introduction);
        //introduction.setText("Introduction:\nYou need to RECORD your voice loudly three times!\n\n ");


        startbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startrec();
            }
        });

//        endbt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                endrec();
//            }
//        });

    }


    private void startrec(){


        //开始监测，先准备好用麦克风录音，
        if(mp==null){
            File dir=new File(Environment.getExternalStorageDirectory(),"sound");    //指定录音输出文件的文件夹（最后会删除录音文件的）
            if(!dir.exists()){                                                                                                        //文件夹路径不存在就创建一个
                dir.mkdirs();
            }
            soundFile=new File(dir,System.currentTimeMillis()+".amr");                      //创建输出文件
            if(!soundFile.exists()){
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            mp=new MediaRecorder();                                                                                //程序中操作的MediaRecorder类


            mp.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);                            //MediaRecorder类的初始化（注意顺序不能反
            mp.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            mp.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            mp.setOutputFile(soundFile.getAbsolutePath());                       //MediaRecorder和文件绑定，MediaRecorder录制的内容将自动保存在soundFile文件中
            try {
                mp.prepare();
                mp.start();                                                                                                //开始录音
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Toast.makeText(VoiceDetector.this,"Start Recording...", Toast.LENGTH_SHORT).show(); //标签提示一下
            starttorec();                                                                                                          //调用线程来实现获取当前音频振幅 （start to record）

        }

    }
    private Handler mHandler =new Handler(){
        public void handleMessage(Message msg){
            txt1.setText("Current: " + msg.arg1);                                                 //把Handle 中获得的信息在主线程使用，更新txt1的显示内容
            if(maxx<msg.arg1){                                                                     //（注意在setText（）中要加""，否则又会跪（使用是跪了几次才发现= =）
                maxx=msg.arg1;                                                                     //显示最大值
               // maxtext.setText("Max: " + maxx);
            }
            starttorec();
            if(maxx>=327){
                countTimes++;
               // completeTimes.setText("\nCurrently Completed:" + countTimes + "\nPlease start again");
                completeTimes.setText(String.valueOf( countTimes) );
                maxx=0;
                endrec();
                if(countTimes==times){
                    MainActivity.Flag=1;
                /*    Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
                    PendingIntent pintent = PendingIntent.getService(VoiceDetector.this, 0, intent, 0);
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    stopService(intent);
                    alarm.cancel(pintent);*/

                    finish();
                }
            }

                                                                                                     //重新调用检测的线程
        }
    };

    public  void starttorec(){

        timer=new Timer();
        task=new TimerTask(){                                                                                              //设置线程抽象类中的run（），这里更新value的值
            @Override                                                                                                        //把value的值放到用于线程之间交流数据的Handler的message里
            public void run() {
                value=mp.getMaxAmplitude();
                Message message=mHandler.obtainMessage();
                message.arg1=(int)(value/100);
                mHandler.sendMessage(message);                                                     //Handler类发出信息
            }

        };
        timer.schedule(task, 100);                                                                 //timer，设置为100毫秒后执行task线程（会自动调用task的start（）函数）
        //timer是计时器，作用就是在设定时间后启动规定的线程。这用来限制
        //getMaxAmplitude（）的调用频率，减少资源的使用（时间调太短，也会闪退）
    }

    private void endrec(){
        if(mp!=null){
//            AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//            int x=am.getStreamVolume(AudioManager.STREAM_MUSIC);
//            Log.d("volume", String.valueOf(x));
//            for(int i=x;i<=13;i++) {
//                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
//            }
            Toast.makeText(VoiceDetector.this,"Record end...Complete No."+String.valueOf(countTimes)+" loudly Voice", Toast.LENGTH_SHORT).show();          //提示
            timer.cancel();                                                                                                                               //取消计时器（线程将不会被启动）
            mp.stop();                                                                                                                                        //停止录音
            mp.release();                                                                                                                                   //释放资源（mp不再绑定soundFile文件）
            soundFile.delete();                                                                                                                         //删除刚才录下的文件节约空间（也可以不删拿出来听一听~）
            mp=null;
                                                                                                                                                  //习惯性赋空值
        }
    }

    @Override
    public void onBackPressed() {
    }

}
