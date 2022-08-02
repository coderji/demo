package com.ji.libs;

public class Native {
    private static final String TAG = "Native";

    static {
        System.loadLibrary("demo");
    }

    public static native String stringFromJNI();
}
