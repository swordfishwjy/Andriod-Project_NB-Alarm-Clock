package com.example.android.camera2basic;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class shake extends Activity implements SensorEventListener {

    private static final String TAG = "VibrateActivity";

    private SensorManager mSensorManager;
    private Sensor mAccelerator;
    private Sensor mGravity;
    private Sensor mMagnetic;

    private RelativeLayout mGuide;
    private TextView mShakeTimes;
    private TextView mIntro;

    private Integer times = 0; //we need to shake suddenly for ten times
    private double shaking = 0;
    private float acce[] = new float[3];
    private float grav[] = new float[3];
    private float magn[] = new float[3];
    private float earthAcce[] = new float[4];
    private float moveRequired[][] = new float[][]{{1,1,0,},{1,-1,0},{-1,0,0}};

    private boolean shake = true, move = true;
    public static boolean mTreatItAsCloseAlarmButton = false;

    private double X;
    private float Y;
    private float Orien[] = new float[3];
    private SQLiteDatabase db2;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
		
		times = 0;

        MyAlarmService.mvibrator.cancel();
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

        mShakeTimes = (TextView) findViewById(R.id.shakeTimes);
        mIntro = (TextView)findViewById(R.id.introduction);

        if(flag == 4){
            //shake mode
            mIntro.setVisibility(View.VISIBLE);
            mShakeTimes.setText("10");
            shake = false;
        }
        else{
            mShakeTimes.setText("2");
            mGuide = (RelativeLayout)findViewById(R.id.moveArm);
            mGuide.setBackgroundResource(R.drawable.action1);
            //action mode
            shake = true;
        }

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerator, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()){
            case Sensor.TYPE_LINEAR_ACCELERATION:
                acce = event.values.clone();
                break;
            case Sensor.TYPE_GRAVITY:
                grav = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magn = event.values.clone();
                break;
        }

        //shaking mode
        if(!shake){
            shake();
        }
        //gesture mode
        else {
            if(magn != null && grav != null) {
                moving();
            }
        }
    }

    private boolean isPass() {
        //X axis
        if(moveRequired[times%3][0] == 1){
            if(X > 4) {
                //Log.d(TAG, "x is right.");
            }
            else{
                return false;
            }
        }
        else if(moveRequired[times%3][0] == -1){
            if(X < -4){
                //Log.d(TAG, "x is right.");
            }
            else {
                return false;
            }
        }
        else{
            if(Math.abs(X) < 4){
                //Log.d(TAG, "x is right.");
            }
            else {
                return false;
            }
        }

        if(moveRequired[times%3][1] == 1){
            if(Y > 4) {
                //Log.d(TAG, "y is right.");
            }
            else{
                return false;
            }
        }
        else if(moveRequired[times%3][1] == -1){
            if(Y < -4){
                //Log.d(TAG, "y is right.");
            }
            else {
                return false;
            }
        }
        else{
            if(Math.abs(Y) < 4){
                //Log.d(TAG, "y is right.");
            }
            else {
                return false;
            }
        }
        return true;
    }

    private void moving(){

        float[] Rotation = new float[16];
        float[] I = new float[16];

        mSensorManager.getRotationMatrix(Rotation, I, grav, magn);
        mSensorManager.getOrientation(Rotation, Orien);
        if(Orien == null){
            Orien[0] = (float) Math.toDegrees(Orien[0]);
            Log.d(TAG, "orientation: " + Orien[0]);
            double cos = Math.cos(Orien[0]);
            double sin = Math.sin(Orien[0]);
            Log.d(TAG, "cos = " + cos + ", sin = " + sin);
        }

        float[] relacce = new float[4];
        float[] inver = new float[16];

        relacce[0] = acce[0];
        relacce[1] = acce[1];
        relacce[2] = acce[2];
        relacce[3] = 0;

        android.opengl.Matrix.invertM(inver, 0, Rotation, 0);
        android.opengl.Matrix.multiplyMV(earthAcce, 0, inver, 0, relacce, 0);

        float x = earthAcce[0];
        float y = earthAcce[1];
        float z = earthAcce[2]; // 这是我们要的y轴值
        //Log.d(TAG, x + "," + y + "," + z);

        //move = true means the phone is moving
        if (move == true && (Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10)){
            //transform the real xyz axis to the xyz axis we assumed
            move = false;
            X = x * Math.cos(Orien[0]) + y * Math.sin(Orien[0]);
            Y = z;
            Log.d(TAG, "X = " + X + ", Y = ");
            Log.d(TAG, "x = " + x + ", z = " + z + ", y = " + y);
        }

        //move change to false means the data of moving is finished
        if((Math.abs(x) < 2 || Math.abs(y) < 2 && Math.abs(z) < 2) && move == false){
            move = true;
            if(isPass()){
                if(times%3 == 0){
                    mGuide.setBackgroundResource(R.drawable.action2);
                }
                else if(times%3 == 1){
                    mGuide.setBackgroundResource(R.drawable.action3);
                }
                else if (times%3 == 2 && times < 6){
                    mGuide.setBackgroundResource(R.drawable.action1);
                }

                Toast.makeText(this, times + " action!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, times + "success");
                times++;
                mShakeTimes.setText(String.valueOf(2 - times / 3));
            }
        }

        if (times == 6){
            mGuide.setBackgroundResource(R.drawable.or1);
            mShakeTimes.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "over", Toast.LENGTH_SHORT).show();
            ending();
        }
    }

    //private class
    private void shake(){
        shaking = Math.sqrt(acce[0]*acce[0] + acce[1]*acce[1] + acce[2]*acce[2]);

        if (shaking > 15 && move == true){
            move = false;
        }
        else if(move == false && shaking < 2){
            times++;
            if(times < 10){
                mShakeTimes.setText(String.valueOf(10 - times));
            }
            move = true;
        }

        if (times == 10){
            ending();
        }
    }

    private void ending(){
        //TODO: close the alarm
        mSensorManager.unregisterListener(this);
        //close the alarm
        mTreatItAsCloseAlarmButton = true;

        MainActivity.Flag = 1;
//        Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
//        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        stopService(intent);
//        alarm.cancel(pintent);
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerator, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
	
	

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onBackPressed() {
    }
}

