package com.ji.demo;

import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ji.utils.LogUtils;

public class CaseFragment extends BaseFragment {
    private static final String TAG = "CaseFragment";

    static {
        System.loadLibrary("demo");
    }

    public static native String getUnlockCode(String sn);
    public static native String getCrash();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.case_je).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "case_je");
                throw new AndroidRuntimeException("Java Crash");
            }
        });
        view.findViewById(R.id.case_ne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "case_ne");
                getCrash();
            }
        });
        view.findViewById(R.id.case_anr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "case_anr");
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
