package com.ji.algorithm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ji.utils.FixBugUtils;
import com.ji.utils.LogUtils;
import com.ji.utils.ReflectUtils;

import java.util.List;

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
                    // startForegroundService(new Intent(getApplicationContext(), MainService.class));
                    sendNotify("xx");
                } else {
                    // stopService(new Intent(getApplicationContext(), MainService.class));
                    cancelNotify();
                }
                mStart = !mStart;
            }
        });

        Button button = findViewById(R.id.main_btn);
//        button.setText("Fix bug.");
//        FixBugUtils.loadAssets(this);
//        button.setText(getString((int) ReflectUtils.getField("com.ape.easymode.R$string", "always", null)));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "startActivity SingleInstanceActivity", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, SingleInstanceActivity.class));
                    }
                }, 5 * 1000);
//                queryApps();
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
                PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_DIAL), 0);
                Notification.Action.Builder actionBuilder = new Notification.Action.Builder(
                        Icon.createWithResource(this, R.mipmap.ic_launcher),
                        "title",
                        intent);
                builder.addAction(actionBuilder.build());
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

    private void queryApps() {
        LogUtils.v(TAG, "queryApps");
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo info : apps) {
            LogUtils.v(TAG, "queryApps " + info);
        }
    }
}
