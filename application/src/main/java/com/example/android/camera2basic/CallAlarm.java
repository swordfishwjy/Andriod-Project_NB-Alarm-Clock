package com.example.android.camera2basic;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;

public class CallAlarm extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent) {

    Log.i("broadcast",String.valueOf(MainActivity.active));

   // if (MainActivity.active) {
      Intent music_service = new Intent(context, MyAlarmService.class);
      context.startService(music_service);
      for (int j = 0; j < 1000; j++) {
        for (int m = 0; m < 1000; m++) {

        }
      }
      Intent i = new Intent(context, AlarmAlert.class);
      Bundle bundleRet = new Bundle();
      bundleRet.putString("STR_CALLER", "");
      i.putExtras(bundleRet);
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);

      Log.i("MYRECEIVER", "ON RECEIVE");
    }
 // }

}
