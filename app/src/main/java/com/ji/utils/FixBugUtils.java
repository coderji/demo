package com.ji.utils;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class FixBugUtils {
    private static String TAG = "FixBugUtils";

    public static void load(Context context) {
        Object oldDexPathList = ReflectUtils.getField(BaseDexClassLoader.class, context.getClassLoader(), "pathList");
        Object[] oldDexElements = (Object[]) ReflectUtils.getField("dalvik.system.DexPathList", oldDexPathList, "dexElements");
        LogUtils.v(TAG, "oldDexElements:" + Arrays.toString(oldDexElements));

        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            String dexPath = externalCacheDir.getPath() + File.separator + "app-debug.apk";
            Object newDexPathList = ReflectUtils.getField(BaseDexClassLoader.class,
                    new DexClassLoader(dexPath, null, null, null),
                    "pathList");
            Object[] newDexElements = (Object[]) ReflectUtils.getField("dalvik.system.DexPathList", newDexPathList, "dexElements");
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
                ReflectUtils.setField("dalvik.system.DexPathList", oldDexPathList, "dexElements", mergeDexElements);
            }
        }
    }
}
