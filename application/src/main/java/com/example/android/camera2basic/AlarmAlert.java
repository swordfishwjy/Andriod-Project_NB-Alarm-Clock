package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlarmAlert extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new AlertDialog.Builder(AlarmAlert.this)
                .setIcon(R.drawable.clock)
                .setTitle("WakeUp!!!")
                .setMessage("It's time to wake up!!!")
                .setPositiveButton("Turn OFF",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                AlarmAlert.this.finish();
                            }
                        })
                .setCancelable(false)
                .show();
    }
}
