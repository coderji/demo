package com.ji.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class FixBugUtils {
    private static String TAG = "FixBugUtils";

    public static void loadDex(Context context) {
        Object oldDexPathList = ReflectUtils.getField(BaseDexClassLoader.class, "pathList", context.getClassLoader());
        Object[] oldDexElements = (Object[]) ReflectUtils.getField("dalvik.system.DexPathList", "dexElements", oldDexPathList);
        LogUtils.v(TAG, "oldDexElements:" + Arrays.toString(oldDexElements));

        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            String dexPath = externalCacheDir.getPath() + File.separator + "app-debug.apk";
            Object newDexPathList = ReflectUtils.getField(BaseDexClassLoader.class,
                    "pathList",
                    new DexClassLoader(dexPath, null, null, null));
            Object[] newDexElements = (Object[]) ReflectUtils.getField("dalvik.system.DexPathList", "dexElements", newDexPathList);
            LogUtils.v(TAG, "newDexElements:" + Arrays.toString(newDexElements));

            // merge dexElements
            if (oldDexElements != null && oldDexElements.length > 0
                    && newDexElements != null && newDexElements.length > 0) {
                int newDexElementsLength = newDexElements.length;
                int oldDexElementsLength = oldDexElements.length;
                Object mergeDexElements = Array.newInstance(newDexElements[0].getClass(), newDexElementsLength + oldDexElementsLength);
                for (int i = 0; i < newDexElementsLength; i++) {
                    Array.set(mergeDexElements, i, Array.get(newDexElements, i));
                }
                for (int i = 0; i < oldDexElementsLength; i++) {
                    Array.set(mergeDexElements, newDexElementsLength + i, Array.get(oldDexElements, i));
                }
                LogUtils.v(TAG, "mergeDexElements:" + Arrays.toString((Object[]) mergeDexElements));
                ReflectUtils.setField("dalvik.system.DexPathList", "dexElements", oldDexPathList, mergeDexElements);
            }
        }
    }

    public static void loadAssets(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            ReflectUtils.invoke(context.getAssets(),
                    ReflectUtils.getMethod(AssetManager.class, "addAssetPath", String.class),
                    externalCacheDir.getPath() + File.separator + "app-debug.apk");
        }
    }
}
