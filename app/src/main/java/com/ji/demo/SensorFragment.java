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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ji.util.Log;

public class SensorFragment extends Fragment {
    private static final String TAG = "SensorFragment";
    private TextView mTextView;
    private ProximityCheck mProximityCheck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextView = view.findViewById(R.id.sensor_info);
        mProximityCheck = new ProximityCheck(view.getContext(), new Handler()) {
            @Override
            public void onResult(boolean isNear) {
                mTextView.setText("isNear " + isNear);
            }
        };
        view.findViewById(R.id.sensor_register).setOnClickListener((v) -> mProximityCheck.register());
        view.findViewById(R.id.sensor_unregister).setOnClickListener((v) -> mProximityCheck.unregister());
    }

    private static class ProximityCheck {
        private final SensorManager mSensorManager;
        private final SensorEventListener mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values.length == 0) {
                    Log.w(TAG, "ProximityCheck Event has no values!");
                    onResult(false);
                } else {
                    boolean isNear = event.values[0] == 0;
                    Log.d(TAG, "ProximityCheck isNear:" + isNear);
                    onResult(isNear);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        private final Handler mHandler;
        private boolean mRegistered;

        public void onResult(boolean isNear) {

        }

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
}
