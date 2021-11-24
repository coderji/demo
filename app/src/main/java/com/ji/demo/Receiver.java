package com.ji.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ji.util.Log;

public class Receiver extends BroadcastReceiver {
    private static final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive " + intent.getAction());
    }
}
