package com.ji.algorithm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ji.utils.LogUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_ID = 10;
    private boolean mStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getApplicationContext(), ListActivity.class));
                if (!mStart) {
                    startForegroundService(new Intent(getApplicationContext(), MainService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), MainService.class));
                }
                mStart = !mStart;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy");
    }

    private void sendNotify(String name) {
        LogUtils.v(TAG, "sendNotify");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setContentText("ContentText1");
            builder.setSmallIcon(R.mipmap.ic_launcher);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel =
                        new NotificationChannel(getPackageName(), "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
                builder.setChannelId(notificationChannel.getId());
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build());
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
