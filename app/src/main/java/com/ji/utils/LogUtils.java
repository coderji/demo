package com.ji.utils;

import com.ji.algorithm.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtils {
    private static final String TAG = "App";
    private static boolean sPhone = true;

    static {
        try {
            android.util.Log.v(TAG, "sPhone:" + sPhone);
        } catch (Exception e) {
            sPhone = false;
        }
    }

    public static boolean isPhone() {
        return sPhone;
    }

    private static void println(String level, String tag, String msg) {
        System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                + " " + Thread.currentThread().getName()
                + " " + level + " " + tag + " - " + msg);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE)) {
            if (isPhone()) {
                android.util.Log.v(TAG, tag + " - " + msg);
            } else {
                println("V", tag, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (isPhone()) {
            android.util.Log.d(TAG, tag + " - " + msg);
        } else {
            println("D", tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isPhone()) {
            android.util.Log.e(TAG, tag + " - " + msg);
        } else {
            println("E", tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isPhone()) {
            android.util.Log.e(TAG, tag + " - " + msg, tr);
        } else {
            println("E", tag, msg);
            tr.printStackTrace();
        }
    }
}
