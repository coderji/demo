package com.ji.terminal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SyncTest {
    private static final String TAG = "SyncTest";
    private int mDisable = 1;
    private final Object mLock = new Object();

    private void setDisabled(int disable) {
        log("setDisabled disable:" + disable);
        mDisable = disable;
    }

    private int getDisabled() {
        return mDisable;
    }

    private void disable(int disable) {
        synchronized (mLock) {
            log("disable disable:" + disable);
            sleep(100);
            setDisabled(disable);
        }
    }

    private void recomputeDisableFlags() {
//        synchronized (mLock) {
            int disable = getDisabled();
            log("recomputeDisableFlags disable:" + disable);
            disable(disable);
//        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void log(String s) {
        System.out.println(String.format(Locale.getDefault(), "%-10s", Thread.currentThread().getName())
                + new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                + " " + TAG + ": " + s);
    }

    public static int main(String[] args) {
        SyncTest testUtils = new SyncTest();
        new Thread(() -> {
            testUtils.disable(0);
        }).start();
        sleep(50);
        new Thread(testUtils::recomputeDisableFlags).start();
        sleep(1000);
        log("getDisabled:" + testUtils.getDisabled());
        return 0;
    }
}
