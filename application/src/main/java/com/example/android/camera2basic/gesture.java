package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class gesture extends Activity {

    public FrameLayout screen;
    public Paint mPathPaint,mPathPaint2,mCirPaint;
    private mdrawpath mpath;
    private mdrawpath2 mpath2;
    public Path path,path2;
    public PathEffect effect;
    float x,y,x1[],y1[],x2[],y2[],cx,cy;
    private SQLiteDatabase db;
    public int i,j,m;
    public static int ck[]={0,0,0};
    public String gesture_db[]={"gesture1","gesture2"};
    private circle mcircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        screen = (FrameLayout)findViewById(R.id.screen);
        mCirPaint = new Paint(0);
        mCirPaint.setStyle(Paint.Style.FILL);
        mcircle = new circle(this);
        mpath = new mdrawpath(this);
        mpath2 = new mdrawpath2(this);
        mPathPaint = new Paint();
        mPathPaint.setColor(Color.GREEN);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(20);
        mPathPaint.setAlpha(255);
        mPathPaint2 = new Paint();
        mPathPaint2.setColor(Color.RED);
        mPathPaint2.setStyle(Paint.Style.STROKE);
        mPathPaint2.setStrokeWidth(20);
        mPathPaint2.setAlpha(50);
        path = new Path();
        path2 =new Path();
        effect = new CornerPathEffect(5);
        mPathPaint.setPathEffect(effect);
        db = openOrCreateDatabase("gesture.db", Context.MODE_PRIVATE, null);
        db.close();
        copyDB();
    //    db = openOrCreateDatabase("gesture.db", Context.MODE_PRIVATE, null);
    //    db.execSQL("Drop Table if exists gesture");
    //    db.execSQL("Create Table if not exists gesture (x float not null, y float not null)");
    //    db.execSQL("Create Table if not exists gesture2 (x float not null, y float not null)");
        x1 = new float[5000];
        x2 = new float[5000];
        y1 = new float[5000];
        y2 = new float[5000];
    //    show2();


    }

    public void copyDB(){
        File data = Environment.getDataDirectory();
        String currentDBPath = "/data/"+ "com.example.android.camera2basic" +"/databases/gesture.db";
        String filepath = "/data/"+ "com.example.android.camera2basic" +"/databases";
    /*    File dir = new File(filepath);
        try {
            if (!dir.exists())
                dir.mkdir();
        }catch (Exception e) {
            Log.i("error:", e + "");
        }*/
        File databaseFilename = new File(data, currentDBPath);
    /*    try{
            if(!databaseFilename.exists())
                databaseFilename.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }*/
        try {
        //    if (!(databaseFilename).exists()) {
            InputStream is = getResources().openRawResource(R.raw.gesture);
            FileOutputStream fos = new FileOutputStream(databaseFilename);
            byte[] buffer = new byte[7168];
            int count = 0;

            while ((count = is.read(buffer)) > 0)
            {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        //    Toast.makeText(this, "lalala", Toast.LENGTH_LONG).show();
        /*    }
            else
                Toast.makeText(this,"666",Toast.LENGTH_LONG).show();*/
        } catch(Exception e) {
            e.printStackTrace();
        }
        db = openOrCreateDatabase("gesture.db", Context.MODE_PRIVATE, null);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("ck",String.valueOf(ck[0])+String.valueOf(ck[1]));
        show2();
        screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
        //        mPathPaint.setAlpha(255);
            /*    x1=new float[500];
                y1=new float[500];
                i=0;*/
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        db.execSQL("Drop Table if exists gesture");
                        db.execSQL("Create Table if not exists gesture (x float not null, y float not null)");
                        //    if(!path.isEmpty())
                        if (ck[0] == 1) {
                            screen.removeView(mpath);
                            ck[0] = 0;
                        }
                        path = new Path();
                        x = event.getX();
                        y = event.getY();
                        x1[i] = x;
                        y1[i] = y;
                        db.execSQL("INSERT INTO gesture VALUES (?,?)", new Object[]{x, y});
                        path.moveTo(x, y);
                        screen.addView(mpath);
                        ck[0] = 1;
                        i++;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (ck[0] == 1) {
                            screen.removeView(mpath);
                            ck[0] = 0;
                        }
                        x = event.getX();
                        y = event.getY();
                        db.execSQL("INSERT INTO gesture VALUES (?,?)", new Object[]{x, y});
                        x1[i] = x;
                        y1[i] = y;
                        path.lineTo(x, y);
                        screen.addView(mpath);
                        ck[0] = 1;
                        i++;
                        break;
                    case MotionEvent.ACTION_UP:
                        m = i;
                        //    Toast.makeText(gesture.this,String.valueOf(m),Toast.LENGTH_SHORT).show();
                        i = 0;
                        compare2();
                        //    screen.removeView(mpath);
                        break;
                }
                return true;
            }
        });
    }

    public class mdrawpath extends View {

        public mdrawpath(Context context) {
            super(context);
        }

        public mdrawpath(Context context, AttributeSet attrs){
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, mPathPaint);

        }
    }

    public class mdrawpath2 extends View {

        public mdrawpath2(Context context) {
            super(context);
        }

        public mdrawpath2(Context context, AttributeSet attrs){
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path2, mPathPaint2);

        }
    }


    @Override
    public void onStop(){
        super.onStop();
    /*    ck[0]=0;
        ck[1]=0;*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    /*    ck[0]=0;
        ck[1]=0;*/
        db.close();
    }

/*    public void exportDB(View view) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/"+ "com.example.android.camera2basic" +"/databases/gesture.db";
        String backupDBPath = "gesture.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }*/
    /*    try {
            source = new FileInputStream(backupDB).getChannel();
            destination = new FileOutputStream(currentDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Imported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }*/
//    }

/*    public void show(View view){
        int ges = (int)(Math.random()*3);
        if(ck[1]==1) {
        //    if(!path.isEmpty())
            screen.removeView(mpath2);
            ck[1]=0;

        }
    //    if(!path.isEmpty())
    //        screen.removeView(mpath);
        path = new Path();
        mPathPaint.setAlpha(20);
        j=0;
        Cursor c = db.query(gesture_db[ges],null,null,null,null,null,null);
        if(c.getCount()!=0) {
            if (c.moveToFirst()) {//check is null or not
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    x = c.getFloat(c.getColumnIndex("x"));
                    y = c.getFloat(c.getColumnIndex("y"));
                    if(c.isFirst())
                        path.moveTo(x,y);
                    else
                        path.lineTo(x, y);
                    x2[j]=x;
                    y2[j]=y;
                    j++;

                }
            }
            c.close();
            screen.addView(mpath2);
            ck[1]=1;
        }
        else{
            Toast.makeText(this,"Draw a gesture first.",Toast.LENGTH_LONG).show();
        }
    }*/

    public void show2(){
        int ges = (int)(Math.random()*2);
        if(ck[1]==1) {
            //    if(!path2.isEmpty())
            screen.removeView(mpath2);
            ck[1]=0;

        }
        path2 = new Path();
    //    mPathPaint.setAlpha(20);
        j=0;
        if(ck[2]==1){
            screen.removeView(mcircle);
            ck[2]=0;
        }
        Cursor c = db.query(gesture_db[ges],null,null,null,null,null,null);
        if(c.getCount()!=0) {
            if (c.moveToFirst()) {//check is null or not
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    x = c.getFloat(c.getColumnIndex("x"));
                    y = c.getFloat(c.getColumnIndex("y"));
                    if(c.isFirst()) {
                        path2.moveTo(x, y);
                        cx = x;
                        cy = y;
                    }
                    else
                        path2.lineTo(x, y);
                    x2[j]=x;
                    y2[j]=y;
                    j++;

                }
            }
            c.close();
            screen.addView(mcircle);
            screen.addView(mpath2);
            ck[1]=1;
            ck[2]=1;
        }
        else{
            Toast.makeText(this,"Draw a gesture first.",Toast.LENGTH_LONG).show();
        }
    }


/*    public void compare(View view){
        int k,l,flag;
        float a,d;
        flag=0;
        if(m>j) {
            for (k = 0; k < m; k++) {
                for (l = 0; l < j; l++) {
                    d = (x1[k] - x2[l]) * (x1[k] - x2[l]) + (y1[k] - y2[l]) * (y1[k] - y2[l]);
                    d = (float) Math.sqrt(d);
                    if (d < 20) {
                        flag++;
                        break;
                    }

                }
            }
            a = (float) flag / (float) m;
            Toast.makeText(this, String.valueOf(flag) + ", " + String.valueOf(m) + ", " + String.valueOf(a), Toast.LENGTH_SHORT).show();
            if (a > 0.9) {
                Log.i("gesture", "match");
                MainActivity.Flag=1;
                Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
                PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                stopService(intent);
                alarm.cancel(pintent);

                finish();
            }
        }
        else{
            for (l = 0; l < j; l++) {
                for (k = 0; k < m; k++) {
                    d = (x1[k] - x2[l]) * (x1[k] - x2[l]) + (y1[k] - y2[l]) * (y1[k] - y2[l]);
                    d = (float) Math.sqrt(d);
                    if (d < 20) {
                        flag++;
                        break;
                    }

                }
            }
            a = (float) flag / (float) j;
            Toast.makeText(this, String.valueOf(flag) + ", " + String.valueOf(j) + ", " + String.valueOf(a), Toast.LENGTH_SHORT).show();
            if (a > 0.9) {
            //    Toast.makeText(this, "Match!", Toast.LENGTH_LONG).show();
                Log.i("gesture", "match");
                MainActivity.Flag=1;
                Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
                PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                stopService(intent);
                alarm.cancel(pintent);

                finish();
            }
            else
                Toast.makeText(this,"Please try again!",Toast.LENGTH_LONG).show();
        }
    }*/

    public void compare2(){
        int k,l,flag;
        float a,d,d0,d1,d2,d3;
        flag=0;
        d0 = (x1[0] - x2[0]) * (x1[0] - x2[0]) + (y1[0] - y2[0]) * (y1[0] - y2[0]);
        d0 = (float) Math.sqrt(d0);
        d1 = (x1[m-1] - x2[j-1]) * (x1[m-1] - x2[j-1]) + (y1[m-1] - y2[j-1]) * (y1[m-1] - y2[j-1]);
        d1 = (float) Math.sqrt(d1);
        d2 = (x1[0] - x2[j-1]) * (x1[0] - x2[j-1]) + (y1[0] - y2[j-1]) * (y1[0] - y2[j-1]);
        d2 = (float) Math.sqrt(d2);
        d3 = (x1[m-1] - x2[0]) * (x1[m-1] - x2[0]) + (y1[m-1] - y2[0]) * (y1[m-1] - y2[0]);
        d3 = (float) Math.sqrt(d3);
        if(d0 < 45 && d1 < 45) {
            if (m > j) {
                for (k = 0; k < m; k++) {
                    for (l = 0; l < j; l++) {
                        d = (x1[k] - x2[l]) * (x1[k] - x2[l]) + (y1[k] - y2[l]) * (y1[k] - y2[l]);
                        d = (float) Math.sqrt(d);
                        if (d < 30) {
                            flag++;
                            break;
                        }

                    }
                }
                a = (float) flag / (float) m;
                Log.i("result", "more " + String.valueOf(flag) + ", " + String.valueOf(m) + ", " + String.valueOf(a));
                //    Toast.makeText(this, String.valueOf(flag) + ", " + String.valueOf(m) + ", " + String.valueOf(a), Toast.LENGTH_SHORT).show();
                if (a > 0.9) {
                    Log.i("gesture", "match");
                    MainActivity.Flag = 1;
                /*    Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
                    PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    stopService(intent);
                    alarm.cancel(pintent);*/


                    finish();
                } else
                    Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show();
            } else {
                for (l = 0; l < j; l++) {
                    for (k = 0; k < m; k++) {
                        d = (x1[k] - x2[l]) * (x1[k] - x2[l]) + (y1[k] - y2[l]) * (y1[k] - y2[l]);
                        d = (float) Math.sqrt(d);
                        if (d < 30) {
                            flag++;
                            break;
                        }

                    }
                }
                a = (float) flag / (float) j;
                Log.i("result", "less " + String.valueOf(flag) + ", " + String.valueOf(m) + ", " + String.valueOf(a));
                //    Toast.makeText(this, String.valueOf(flag) + ", " + String.valueOf(j) + ", " + String.valueOf(a), Toast.LENGTH_SHORT).show();
                if (a > 0.9) {
                    //    Toast.makeText(this, "Match!", Toast.LENGTH_LONG).show();
                    Log.i("gesture", "match");
                    MainActivity.Flag = 1;
                /*    Intent intent = new Intent(MainActivity.getAppContext(), MyAlarmService.class);
                    PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    stopService(intent);
                    alarm.cancel(pintent);*/

                    finish();
                } else
                    Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    /*    else if(d2 < 50 && d3 < 50)
            Toast.makeText(this,"Please change another start point!",Toast.LENGTH_LONG).show();*/
        else
            Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show();
    }

    public class circle extends View {

        public circle(Context context) {
            super(context);
        }

        public circle(Context context, AttributeSet attrs){
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            mCirPaint.setColor(Color.RED);
            canvas.drawCircle(cx, cy, 30, mCirPaint);

        }
    }

/*    public void clear(View view){
        if(ck[0]==1){
            screen.removeView(mpath);
            ck[0]=1;
        }
        if(ck[1]==1){
            screen.removeView(mpath2);
            ck[1]=1;
        }

    }*/
    @Override
    public void onBackPressed() {
    }
}
