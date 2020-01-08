package com.ji.algorithm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ji.utils.LogUtils;

public class ForegroundServiceFragment extends Fragment {
    private static final String TAG = "ForegroundServiceFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fg_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fg_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() != null) {
                    getContext().startService(new Intent(getContext(), MyService.class));
                }
            }
        });
        view.findViewById(R.id.fg_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() != null) {
                    getContext().stopService(new Intent(getContext(), MyService.class));
                }
            }
        });
    }

    public static class MyService extends Service {
        private static final String TAG = "MyService";
        public static final int ID = 1;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            LogUtils.v(TAG, "onCreate");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LogUtils.v(TAG, "onDestroy");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);
            LogUtils.v(TAG, "onStartCommand flags:" + flags + " startId:" + startId);

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                Notification.Builder builder = new Notification.Builder(this, TAG);
                builder.setContentText(TAG);
                builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

                NotificationChannel channel =
                        new NotificationChannel(getPackageName(),
                                "MyServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channel.getId());

                startForeground(ID, builder.build());
            }

            return START_STICKY;
        }
    }
}
