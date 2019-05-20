package com.ji.algorithm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.ji.utils.FixBugUtils;
import com.ji.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainApplication extends Application {
    private String TAG = "MainApplication";
    private ArrayList<WeakReference<Activity>> mWeakActivity = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

//        FixBugUtils.loadDex(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    public void destroyActivity(Class c) {
        for (WeakReference<Activity> weakReference : mWeakActivity) {
            Activity weakActivity = weakReference.get();
            if (weakActivity != null
                    && weakActivity.getClass().getSimpleName().equals(c.getSimpleName())) {
                weakActivity.finish();
                mWeakActivity.remove(weakReference);
                break;
            }
        }
    }

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LogUtils.v(TAG, "onActivityCreated activity:" + activity);
            mWeakActivity.add(new WeakReference<>(activity));
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            LogUtils.v(TAG, "onActivityDestroyed activity:" + activity);
            for (WeakReference<Activity> weakReference : mWeakActivity) {
                Activity weakActivity = weakReference.get();
                if (weakActivity != null
                        && weakActivity.getClass().getSimpleName().equals(activity.getClass().getSimpleName())) {
                    mWeakActivity.remove(weakReference);
                    break;
                }
            }
        }
    };
}
