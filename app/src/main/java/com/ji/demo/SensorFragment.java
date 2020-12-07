package com.ji.demo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ji.util.Log;

import java.util.List;

public class SensorFragment extends Fragment {
    private static final String TAG = "SensorFragment";
    private ProximityCheck mProximityCheck;
    private SarCheck mSarCheck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProximityCheck = new ProximityCheck(view.getContext(), new Handler());
        mSarCheck = new SarCheck(view.getContext(), new Handler());
        view.findViewById(R.id.sensor_register).setOnClickListener((v) -> mSarCheck.register());
        view.findViewById(R.id.sensor_unregister).setOnClickListener((v) -> mSarCheck.unregister());
    }

    private static class ProximityCheck {
        private final SensorManager mSensorManager;
        private final SensorEventListener mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values.length == 0) {
                    Log.w(TAG, "ProximityCheck Event has no values!");
                } else {
                    boolean isNear = event.values[0] == 0;
                    Log.d(TAG, "ProximityCheck isNear:" + isNear);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        private final Handler mHandler;
        private boolean mRegistered;

        ProximityCheck(Context context, Handler handler) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mHandler = handler;
        }

        public void register() {
            Log.d(TAG, "ProximityCheck register");
            if (!mRegistered) {
                mRegistered = true;
                mSensorManager.registerListener(mSensorEventListener,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL, mHandler);
            }
        }

        public void unregister() {
            Log.d(TAG, "ProximityCheck unregister");
            if (mRegistered) {
                mRegistered = false;
                mSensorManager.unregisterListener(mSensorEventListener);
            }
        }
    }

    private static class SarCheck {
        private final SensorManager mSensorManager;
        private final SensorEventListener mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values.length == 0) {
                    Log.w(TAG, "SarCheck Event has no values!");
                } else {
                    boolean isNear = event.values[0] == 0;
                    Log.d(TAG, "SarCheck " + event.sensor.getName() + " " + event.values[0]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        private final Handler mHandler;
        private boolean mRegistered;

        SarCheck(Context context, Handler handler) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mHandler = handler;
        }

        public void register() {
            Log.d(TAG, "SarCheck register, device:" + Build.DEVICE);
            if (!mRegistered) {
                mRegistered = true;
                if ("capri".equals(Build.DEVICE)) {
                    List<Sensor> sensorList = mSensorManager.getSensorList(0x10010);
                    int size = sensorList == null ? 0 : sensorList.size();
                    Log.d(TAG, "SarCheck sensorList:" + sensorList);
                    for (int i = 0; i < size; i++) {
                        mSensorManager.registerListener(mSensorEventListener,
                                sensorList.get(i), SensorManager.SENSOR_DELAY_NORMAL, mHandler);
                    }
                } else if ("P352".equals(Build.DEVICE)) {
                    Sensor topSar = mSensorManager.getDefaultSensor(0x10030);
                    mSensorManager.registerListener(mSensorEventListener, topSar, SensorManager.SENSOR_DELAY_NORMAL, mHandler);

                    Sensor bottomSar = mSensorManager.getDefaultSensor(0x10031);
                    mSensorManager.registerListener(mSensorEventListener, bottomSar, SensorManager.SENSOR_DELAY_NORMAL, mHandler);
                }
            }
        }

        public void unregister() {
            Log.d(TAG, "SarCheck unregister");
            if (mRegistered) {
                mRegistered = false;
                mSensorManager.unregisterListener(mSensorEventListener);
            }
        }
    }
}
