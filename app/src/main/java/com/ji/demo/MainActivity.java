package com.ji.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "Demo-MainActivity";
    private NotificationManager mNotificationManager;
    private static final int ID = 0;

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

    private void testBindService() {
        Intent intent = new Intent();
        intent.setAction("com.ji.demo.AIDL_SERVICE");
        intent.setPackage("com.ji.demo");
        bindService(intent, mDemoConnection, BIND_AUTO_CREATE);
    }

    private void testUnbindService() {
        unbindService(mDemoConnection);
    }

    private void testGetInstalledApplications() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo info : apps) {
            Log.v(TAG, "testGetInstalledApplications " + info);
        }
    }

    private void testSendNotify() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Notification.Builder builder = new Notification.Builder(this, TAG);
        builder.setContentText(TAG);
        builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

        NotificationChannel channel =
                new NotificationChannel(getPackageName(),
                        "NTChannel", NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());

        PendingIntent intent = PendingIntent.getActivity(this,
                0, new Intent(Intent.ACTION_DIAL), PendingIntent.FLAG_IMMUTABLE);
        Notification.Action.Builder actionBuilder = new Notification.Action.Builder(
                Icon.createWithResource(this, android.R.mipmap.sym_def_app_icon),
                "Action",
                intent);
        builder.addAction(actionBuilder.build());

        mNotificationManager.notify(ID, builder.build());
    }

    private void testCancelNotify() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        mNotificationManager.cancel(ID);
    }

    private void testBiometric() {
        final BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getBaseContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getBaseContext(),
                        "Authentication succeeded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getBaseContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("NewBiometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        findViewById(R.id.main_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        testBiometric();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
