package com.ji.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class DemoService extends Service {
    private static final String TAG = "Demo-DemoService";
    private String mMsg = "null";

    public DemoService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private final IDemoInterface.Stub mBinder = new IDemoInterface.Stub() {

        @Override
        public String getMessage() throws RemoteException {
            Log.d(TAG, "getMessage " + mMsg);
            return mMsg;
        }

        @Override
        public void setMessage(String msg) throws RemoteException {
            Log.d(TAG, "setMessage " + msg);
            mMsg = msg;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }
}