package com.ji.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private NotificationManager mNotificationManager;
    private static final int ID = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            mNotificationManager = (NotificationManager) getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        view.findViewById(R.id.nt_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotify();
            }
        });
        view.findViewById(R.id.nt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotify();
            }
        });
    }

    private void sendNotify() {
        if (mNotificationManager != null && getContext() != null) {
            Notification.Builder builder = new Notification.Builder(getContext(), TAG);
            builder.setContentText(TAG);
            builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

            NotificationChannel channel =
                    new NotificationChannel(getContext().getPackageName(),
                            "NTChannel", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());

            PendingIntent intent = PendingIntent.getActivity(getContext(),
                    0, new Intent(Intent.ACTION_DIAL), 0);
            Notification.Action.Builder actionBuilder = new Notification.Action.Builder(
                    Icon.createWithResource(getContext(), android.R.mipmap.sym_def_app_icon),
                    "Action",
                    intent);
            builder.addAction(actionBuilder.build());

            mNotificationManager.notify(ID, builder.build());
        }
    }

    private void cancelNotify() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(ID);
        }
    }
}
