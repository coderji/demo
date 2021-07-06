package com.ji.demo;

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

import com.ji.util.BaseFragment;
import com.ji.util.Log;

public class FgServiceFragment extends BaseFragment {
    private static final String TAG = "FgServiceFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fg_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent fgService = new Intent(getContext(), FgService.class);
        view.findViewById(R.id.fg_start).setOnClickListener(v -> v.getContext().startService(fgService));
        view.findViewById(R.id.fg_stop).setOnClickListener(v -> v.getContext().stopService(fgService));
    }

    public static class FgService extends Service {
        private static final String TAG = "FgService";
        public static final int ID = 1;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.v(TAG, "onCreate");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.v(TAG, "onDestroy");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.v(TAG, "onStartCommand");
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                Notification.Builder builder = new Notification.Builder(this, TAG);
                builder.setContentText(TAG);
                builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

                NotificationChannel channel =
                        new NotificationChannel(getPackageName(),
                                "FgServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channel.getId());

                startForeground(ID, builder.build());
            }
            return super.onStartCommand(intent, flags, startId);
        }
    }
}
