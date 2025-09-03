package com.ji.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "Demo-MainActivity";

    static {
        System.loadLibrary("demo");
    }
    public static native String getNativeHello();

    private IDemoInterface mIDemoInterface = null;
    private final ServiceConnection mDemoConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mIDemoInterface = IDemoInterface.Stub.asInterface(service);

            Log.d(TAG, "mIDemoInterface:" + mIDemoInterface);
            try {
                mIDemoInterface.setMessage("hello");
                mIDemoInterface.getMessage();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mIDemoInterface = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        Intent intent = new Intent();
        intent.setAction("com.ji.demo.AIDL_SERVICE");
        intent.setPackage("com.ji.demo");
        bindService(intent, mDemoConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unbindService(mDemoConnection);
    }
}
