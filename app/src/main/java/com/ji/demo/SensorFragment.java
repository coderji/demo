package com.ji.demo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ji.utils.LogUtils;

public class SensorFragment extends BaseFragment {
    private static final String TAG = "SensorFragment";
    private SensorManager mSensorManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSensorManager = (SensorManager) view.getContext().getSystemService(Context.SENSOR_SERVICE);
        view.findViewById(R.id.sensor_register).setOnClickListener(v -> mSensorManager.registerListener(mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL, new Handler()));
        view.findViewById(R.id.sensor_unregister).setOnClickListener(v -> mSensorManager.unregisterListener(mSensorEventListener));
    }

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values.length == 0) {
                LogUtils.w(TAG, "event has no values");
            } else {
                boolean isNear = event.values[0] == 0;
                LogUtils.d(TAG, "isNear:" + isNear);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
