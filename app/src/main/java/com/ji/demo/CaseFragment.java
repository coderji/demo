package com.ji.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ji.util.BaseFragment;
import com.ji.util.Log;

import org.apache.http.ProtocolVersion;

import java.util.Arrays;

public class CaseFragment extends BaseFragment {
    private static final String TAG = "CaseFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        testResources();
        testHttp();
    }

    private void testResources() {
        int id = getResources().getIdentifier("config_biometric_sensors", "array", "android");
        Log.d(TAG, "testResources id:" + id);
        if (id > 0) {
            String[] config_biometric_sensors = getResources().getStringArray(id);
            Log.d(TAG, "testResources config_biometric_sensors:" + Arrays.toString(config_biometric_sensors));
        }
    }

    private void testHttp() {
        Log.d(TAG, "testHttp getClass.classLoader:" + getClass().getClassLoader());
        ProtocolVersion v = new ProtocolVersion("1", 1, 1);
        ClassLoader classLoader = v.getClass().getClassLoader();
        Log.d(TAG, "testHttp ProtocolVersion.classLoader:" + classLoader);
    }
}
