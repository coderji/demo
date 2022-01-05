package com.ji.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ji.util.BaseFragment;
import com.ji.util.Log;

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
        // testHttp();
    }

    private void testResources() {
        int id = getResources().getIdentifier("fingerprint_acquired_vendor", "array", "android");
        Log.d(TAG, "testResources id:" + id);
        if (id > 0) {
            String[] fingerprint_acquired_vendor = getResources().getStringArray(id);
            Log.d(TAG, "testResources fingerprint_acquired_vendor:" + Arrays.toString(fingerprint_acquired_vendor));
        }
    }
}
