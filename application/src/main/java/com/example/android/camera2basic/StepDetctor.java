package com.example.android.camera2basic;

/**
 * Created by aaaaaqi5 on 11/23/2015.
 */
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

//import static com.example.nbalarmclock.ResultsReceiver.getAppContext;

public class StepDetctor extends Activity implements SensorEventListener {

    public static int CURRENT_SETP = 0;

    public static float SENSITIVITY = 5;   //SENSITIVITY

    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;
    private boolean result=false;

    private float lastZ=0;
    private float currentZ=0;
    private float lastY=0;
    private float currentY=0;
    private int flagstep=0;
    private boolean zcount=false;


    private int total_step = 0;
    private int final_total=8;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private SensorManager sensorMan;
    private Sensor accelerometer;
    LinearLayout stepdetectorlayout;


    TextView sensorview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_alarm);
        sensorview = (TextView) findViewById(R.id.showGPSdataTV);
        stepdetectorlayout=(LinearLayout)findViewById(R.id.stepdetectorLayout);

        stepdetectorlayout.getBackground().setAlpha(240);
        Log.i("StepDetector", "on create");
        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //magSensor=sensorMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
       // mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.GRAVITY_EARTH)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));


    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
       // sensorMan.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // Log.i(Constant.STEP_SERVER, "StepDetector");
        Sensor sensor = event.sensor;
        float zvalue=0;
        float yvalue=0;
        boolean analysis=false;
        // Log.i(Constant.STEP_DETECTOR, "onSensorChanged");
        synchronized (this) {
            //noinspection deprecation
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
            } else {
                int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                if (j == 1) {
                    float vSum = 0;
                    zvalue = event.values[2];
                    yvalue = event.values[1];

                    analysis = Zanalysis(zvalue, yvalue);
                    if (analysis) {
                        for (int i = 0; i < 3; i++) {
                            float v = mYOffset + event.values[i] * mScale[j];
                            vSum += v;

                        int k = 0;
                         v = vSum / 3;

                        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                        if (direction == -mLastDirections[k]) {
                            // Direction changed
                            int extType = (direction > 0 ? 0 : 1); // minumum or
                            // maximum?
                            mLastExtremes[extType][k] = mLastValues[k];
                            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                            if (diff > SENSITIVITY) {
                                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                                boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                                boolean isNotContra = (mLastMatch != 1 - extType);

                                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {

                                    end = System.currentTimeMillis();
                                    if (end - start > 500) {
                                        Log.i("StepDetector", "CURRENT_SETP:"
                                                + CURRENT_SETP);
                                        CURRENT_SETP++;
                                        countStep();
                                        mLastMatch = extType;
                                        start = end;

                                    }
                                    mLastMatch = extType;
                                } else {
                                    mLastMatch = -1;
                                }
                            }
                            mLastDiff[k] = diff;
                        }
                        mLastDirections[k] = direction;
                        mLastValues[k] = v;

                    }
                }
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
    }

    private void countStep() {
        if (CURRENT_SETP % 2 == 0) {
            total_step = CURRENT_SETP;
        } else {
            total_step =CURRENT_SETP+1;
        }
            total_step=total_step/2;
        //final_total-=1;

        sensorview.setText(String.valueOf(total_step));
        enoughStep();
        //return total_step;
    }

    public boolean Zanalysis(float z, float y){
        currentZ=z;
        currentY=y;
        float delta1= lastZ-currentZ;
        float delta2=lastY-currentY;
        boolean zcheck;
        boolean ycheck;
        if(z<-5 && z>-10){
          //  Log.i(TAG+"z",String.valueOf(z));
             zcount=true;
            if(y<-10){
                flagstep+=1;
               // Log.i(TAG+"flag",String.valueOf(flag));
            }
        }
        if(z>-5 && zcount){
           // Log.i(TAG+">0",String.valueOf(z));
            if(flagstep<=1){

                result=true;
                flagstep=0;
                zcount=false;
            }
        }

        zcheck=delta1>0 && Math.abs(delta1)>5;
        ycheck=delta2>0;
        //ycheck=true;
        lastY=currentY;
        lastZ=currentZ;
        if(zcheck && ycheck && result){
          //  Log.i("lastZ",String.valueOf(lastZ));
            return true;
        }else
            return false;

    }

    public void enoughStep(){
        if(total_step>=3){
            total_step=0;
            Log.i("StepDetector", "step > 3 is satisfied.");

            /**
             * * chen_start
             */

            MainActivity.Flag=1;
          //  result=true;
            try{
            /*    Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
                PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                stopService(intent);
                alarm.cancel(pintent);*/
                this.finish();
            }catch (NullPointerException e){
                Log.e("ERROR",e.toString());
            }


            /**
             * * chen_end
             */
        }
    }

    @Override
    public void onBackPressed() {
    }

}
