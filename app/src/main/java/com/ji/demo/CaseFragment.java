package com.ji.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ji.util.BaseFragment;
import com.ji.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;

public class CaseFragment extends BaseFragment {
    private static final String TAG = "CaseFragment";

    static {
        System.loadLibrary("demo");
    }

    public static native String getUnlockCode(String sn);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String sn = "78945264";
        Log.d(TAG, "getUnlockCode sn:" + sn + " code:" + getUnlockCode(sn));
    }

    private void invokeResources() {
        int id = getResources().getIdentifier("fingerprint_acquired_vendor", "array", "android");
        Log.d(TAG, "invokeResources id:" + id);
        if (id > 0) {
            String[] fingerprint_acquired_vendor = getResources().getStringArray(id);
            Log.d(TAG, "invokeResources fingerprint_acquired_vendor:" + Arrays.toString(fingerprint_acquired_vendor));
        }
    }

    private void fillData() {
        File data = new File(getContext().getFilesDir(), "fill-data");
        long usableSpace = data.getUsableSpace();
        Log.d(TAG, "fillData usableSpace:" + usableSpace);
        try {
            int BUFF_SIZE = 8 * 1024;
            byte[] bytes = new byte[BUFF_SIZE];
            new Random().nextBytes(bytes);
            RandomAccessFile randomAccessFile = new RandomAccessFile(data, "rws");
            randomAccessFile.seek(randomAccessFile.length());
            Log.d(TAG, "fillData max:" + (int) (usableSpace / BUFF_SIZE));

            while (usableSpace >= BUFF_SIZE) {
                randomAccessFile.write(bytes);
                usableSpace = data.getUsableSpace();
            }
            randomAccessFile.close();
        } catch (Exception e) {
            Log.e(TAG, "fillData", e);
        }
        Log.d(TAG, "fillData done");
    }
}
