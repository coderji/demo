package com.ji.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ji.remotedemo.IRemoteCallback;
import com.ji.remotedemo.IRemoteDemo;
import com.ji.util.BaseFragment;
import com.ji.util.Log;

public class ServiceFragment extends BaseFragment {
    private static final String TAG = "ServiceFragment";
    private static TextView mDataView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Intent fgService = new Intent(getContext(), FgService.class);
        view.findViewById(R.id.service_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "startService");
                // view.getContext().startService(fgService);
                Intent intent = new Intent();
                intent.setClassName("com.ji.remotedemo", "com.ji.remotedemo.ServiceFragment$$FgService");
                view.getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
        view.findViewById(R.id.service_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "stopService");
                // view.getContext().stopService(fgService);
                view.getContext().unbindService(mConnection);
            }
        });
        mDataView = view.findViewById(R.id.service_data);
    }

    private IRemoteDemo mRemoteDemo = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mRemoteDemo = IRemoteDemo.Stub.asInterface(service);
            try {
                mRemoteDemo.register(new IRemoteCallback.Stub() {
                    @Override
                    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

                    }

                    @Override
                    public void dataCallback(String data) throws RemoteException {
                        Log.d(TAG, "dataCallback data:" + data);
                        mDataView.setText(data);
                    }

                    @Override
                    public IBinder asBinder() {
                        return null;
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "mRemoteDemo register", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected");
        }
    };

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
