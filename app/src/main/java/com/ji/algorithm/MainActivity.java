package com.ji.algorithm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean mStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mStart) {
                    startForegroundService(new Intent(getApplicationContext(), MainService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), MainService.class));
                }
                mStart = !mStart;
            }
        });
    }
}
