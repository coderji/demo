package com.ji.demo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ji.util.Log;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_content, new DevicePolicyFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (fragment instanceof BackPressed && ((BackPressed) fragment).onBackPressed()) {
            Log.d(TAG, "onBackPressed fragment:" + fragment);
        } else    {
            super.onBackPressed();
        }
    }

    interface BackPressed {
        boolean onBackPressed();
    }
}
