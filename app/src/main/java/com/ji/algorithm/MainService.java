package com.ji.algorithm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ji.utils.LogUtils;

public class MainService extends Service {
    private static final String TAG = "MainService";
    private static Notification mNotification;
    private static final int NOTIFICATION_ID = 20;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "onCreate");

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentText("ContentText2");
        builder.setSmallIcon(R.mipmap.ic_launcher);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getPackageName(), "Channel2", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder.setChannelId(notificationChannel.getId());
        }

        mNotification = builder.build();
        LogUtils.v(TAG, "->startForeground");
        startForeground(NOTIFICATION_ID, mNotification);

        // start InnerService
        startService(new Intent(this, InnerService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            stopForeground(true);
        }
    }

    public static class InnerService extends Service {
        private static final String TAG = "InnerService";

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            LogUtils.v(TAG, "onCreate");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LogUtils.v(TAG, "->startForeground");
                startForeground(NOTIFICATION_ID, mNotification);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LogUtils.v(TAG, "onDestroy");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                stopForeground(true);
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            LogUtils.v(TAG, "onStartCommand");
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
    }
}
