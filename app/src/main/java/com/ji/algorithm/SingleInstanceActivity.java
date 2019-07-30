package com.ji.algorithm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ji.utils.LogUtils;

import androidx.appcompat.app.AppCompatActivity;

public class SingleInstanceActivity extends AppCompatActivity {
    private static final String TAG = "SingleInstanceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SingleInstanceActivity.this, "startActivity MainActivity", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SingleInstanceActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy");
    }
}
