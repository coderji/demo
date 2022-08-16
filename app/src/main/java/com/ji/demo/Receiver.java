package com.ji.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ji.utils.LogUtils;

public class Receiver extends BroadcastReceiver {
    private static final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, "onReceive " + intent.getAction());
    }
}
