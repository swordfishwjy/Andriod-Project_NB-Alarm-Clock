

package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CameraActivity_preset extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preset);

        Camera2BasicFragment_preset f=new Camera2BasicFragment_preset();
        getFragmentManager().beginTransaction()
                .replace(R.id.container1, f)
                .commit();
    }
    @Override
    public void onBackPressed() {
    }

}
