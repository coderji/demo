package com.ji.demo;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.ji.util.Log;

import java.util.Arrays;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_content, new SensorFragment())
                .commit();
    }

    private void testResources() {
        int id = getResources().getIdentifier("config_biometric_sensors", "array", "android");
        Log.d(TAG, "testResources id:" + id);
        if (id > 0) {
            String[] config_biometric_sensors = getResources().getStringArray(id);
            Log.d(TAG, "config_biometric_sensors:" + Arrays.toString(config_biometric_sensors));
        }
    }
}
