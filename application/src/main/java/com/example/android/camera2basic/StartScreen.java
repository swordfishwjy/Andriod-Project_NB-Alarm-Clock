package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static android.R.color.transparent;

/**
 * Created by aaaaaqi5 on 11/28/2015.
 */
public class StartScreen extends Activity {

    //layout
    TextView setTimeHistoryTV;
    Switch turnOnOffTBT;
    ImageButton trashcanBT;

    // Menu menulayout;
    Calendar c = Calendar.getInstance();

    //vars
    String time1String = null;
    String defalutString = "No Alarm";

    //flags
    private static String TAG = "startscreen";

    private static Context context;
    public static int Flag = 0;
    public static int function_flag = 1;
    String[] modeList = new String[]{"Let System Decide", "Crazy Jump", "Take A Picture",
            "Draw With Fingers", "Shake Your Phone", "Move Your Arms", "Screeming Out Loud"};
    private ArrayList<PopupView> popupViews;
    private PopupListView popupListView;
    private int p;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_start_screen);

        initialList();

        //show history clock setting_time,if no one found--default value will be shown
        setTimeHistoryTV = (TextView) findViewById(R.id.setTimeHistory);
        //trun on or off last alarm
        turnOnOffTBT = (Switch) findViewById(R.id.trunONOFF);
        turnOnOffTBT.getBackground().setAlpha(80);
        //delete previous alarm
       // trashcanBT = (ImageButton) findViewById(R.id.deleteBT);
        //setting button
        SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
        time1String = settings.getString("TIME1", defalutString);
        Log.i(TAG, time1String);
        Log.i(TAG, setTimeHistoryTV.getText().toString());
        Log.i(TAG, String.valueOf(time1String == "NoOne"));
        if (time1String == defalutString) {

            setTimeHistoryTV.setText(defalutString);
            trashcanBT.setVisibility(View.INVISIBLE);
            turnOnOffTBT.setVisibility(View.INVISIBLE);
        } else {
            trashcanBT.setVisibility(View.VISIBLE);
            turnOnOffTBT.setVisibility(View.VISIBLE);
            setTimeHistoryTV.setText(time1String);
        }

        try {
            context = getApplicationContext();
        } catch (NullPointerException e) {
            Log.e("error", e.toString());
        }

        turnofoffbt();
        deletebt();

    }//end of on create

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
                        StartScreen.function_flag = 4;//(int) (Math.random()*5 + 1);
                        break;
                    case 1:
                        StartScreen.function_flag = 1;
                        break;
                    case 2:
                        StartScreen.function_flag = 2;
                        Intent i = new Intent(StartScreen.this, CameraActivity_preset.class);
                        startActivity(i);
                        break;
                    case 3:
                        StartScreen.function_flag = 3;
                        break;
                    case 4:
                        StartScreen.function_flag = 4;
                        break;
                    case 5:
                        StartScreen.function_flag = 5;
                        break;
                    case 6:
                        StartScreen.function_flag = 6;
                        break;
                }
                addclockbt();
                Log.d(TAG, position + " function_flag");
            }
        });
    }

    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1) s = "0" + s;
        return s;
    }

    public void deletebt() {
        trashcanBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTimeHistoryTV.setText(defalutString);
                SharedPreferences time1Share = getPreferences(0);
                SharedPreferences.Editor editor = time1Share.edit();
                editor.putString("TIME1", defalutString);
                editor.commit();
                trashcanBT.setVisibility(View.INVISIBLE);
                turnOnOffTBT.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void turnofoffbt() {
        turnOnOffTBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartScreen.this, CallAlarm.class);
                PendingIntent sender = PendingIntent.getBroadcast(
                        StartScreen.this, 0, intent, 0);
                AlarmManager am;
                am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.cancel(sender);
            }
        });
    }

    public void addclockbt() {
        c.setTimeInMillis(System.currentTimeMillis());
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        new TimePickerDialog(StartScreen.this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);

                //Log.d(TAG, "start run alarm activity");
                Intent intent = new Intent(StartScreen.this, CallAlarm.class);
                PendingIntent sender = PendingIntent.getBroadcast(
                        StartScreen.this, 0, intent, 0);
                AlarmManager am;
                am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP,
                        c.getTimeInMillis(),
                        sender
                );
                String tmpS = format(hourOfDay) + "：" + format(minute);
                setTimeHistoryTV.setText(tmpS);

                //SharedPreferences保存数据，并提交
                SharedPreferences time1Share = getPreferences(0);
                SharedPreferences.Editor editor = time1Share.edit();
                editor.putString("TIME1", tmpS);
                editor.commit();

                Toast.makeText(StartScreen.this, "I'll wake you up at " + tmpS,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }, mHour, mMinute, true).show();
    }

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
}//end of class

