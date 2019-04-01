package com.ji.algorithm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.ji.utils.LogUtils;

public class MainService extends Service {
    private static final String TAG = "MainService";
    private static final int NOTIFICATION_ID = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "onCreate");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                Notification.Builder builder = new Notification.Builder(getApplicationContext(), "ChannelId");
                builder.setContentText("ContentText");
                builder.setSmallIcon(R.mipmap.ic_launcher);

                NotificationChannel notificationChannel =
                        new NotificationChannel(getPackageName(), "ChannelName", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
                builder.setChannelId(notificationChannel.getId());

                notificationManager.notify(NOTIFICATION_ID, builder.build());

                startForeground(NOTIFICATION_ID, new Notification());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy");
    }

    private void sendNotify(String name) {
        LogUtils.v(TAG, "sendNotify");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setContentText("ContentText");
                builder.setSmallIcon(R.mipmap.ic_launcher);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel =
                            new NotificationChannel(getPackageName(), "ChannelName", NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(notificationChannel);
                    builder.setChannelId(notificationChannel.getId());
                }
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } else {
                Notification notification = new Notification();
                notification.tickerText = TAG;
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        }
    }

    private void cancelNotify() {
        LogUtils.v(TAG, "cancelNotify");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
