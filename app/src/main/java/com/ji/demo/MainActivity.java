package com.ji.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "Demo-MainActivity";

    static {
        System.loadLibrary("demo");
    }
    public static native String getCrash();
    public static native String getUnlockCode(String sn);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        biometric();
//        crash();
//        handle();
//        notification();
//        sensor();
        uncaught();
    }

    private void biometric() {
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();
        final BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(getBaseContext()),
                new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
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
                Toast.makeText(getBaseContext(), "Authentication failed", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        Button button = new Button(getBaseContext());
        button.setText("biometric");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(button);
    }

    private void crash() {
        Button je = new Button(getBaseContext());
        je.setText("je");
        je.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new AndroidRuntimeException("JE");
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(je);

        Button ne = new Button(getBaseContext());
        ne.setText("ne");
        ne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCrash();
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(ne);

        Button anr = new Button(getBaseContext());
        anr.setText("anr");
        anr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "anr", e);
                }
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(anr);
    }

    private void handle() {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.d(TAG, "handleMessage " + msg.what);
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "runnable run");
            }
        };

        Button handle = new Button(getBaseContext());
        handle.setText("handle");
        handle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.postDelayed(runnable, 1000);
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(handle);
    }

    private void notification() {
        Notification.Builder builder = new Notification.Builder(getBaseContext(), TAG);
        builder.setContentText(TAG);
        builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

        NotificationManager notificationManager = (NotificationManager) getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel =
                new NotificationChannel("ChannelId", "ChannelName",
                        NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());
        int id = 0;

        PendingIntent intent = PendingIntent.getActivity(getBaseContext(),
                0, new Intent(Intent.ACTION_DIAL), PendingIntent.FLAG_IMMUTABLE);
        Notification.Action.Builder actionBuilder = new Notification.Action.Builder(
                Icon.createWithResource(getBaseContext(), android.R.mipmap.sym_def_app_icon),
                "Dial",
                intent);
        builder.addAction(actionBuilder.build());

        Button send = new Button(getBaseContext());
        send.setText("send");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager.notify(id, builder.build());
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(send);

        Button cancel = new Button(getBaseContext());
        cancel.setText("cancel");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager.cancel(id);
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(cancel);
    }

    private void sensor() {
        SensorManager sensorManager = (SensorManager) getBaseContext()
                .getSystemService(Context.SENSOR_SERVICE);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values.length == 0) {
                    Log.w(TAG, "event has no values");
                } else {
                    boolean isNear = event.values[0] == 0;
                    Log.d(TAG, "isNear:" + isNear);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        Button register = new Button(getBaseContext());
        register.setText("register");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager.registerListener(sensorEventListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(register);

        Button unregister = new Button(getBaseContext());
        unregister.setText("unregister");
        unregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        });
        ((ViewGroup) findViewById(R.id.main_content)).addView(unregister);
    }

    private void uncaught() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                Log.d(TAG, "uncaughtException" + thread.getName(), throwable);
                System.exit(10);
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                throw new AndroidRuntimeException("uncaught");
            }
        }, 2000);
    }
}
