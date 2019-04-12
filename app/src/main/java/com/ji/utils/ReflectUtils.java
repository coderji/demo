package com.ji.utils;

import android.annotation.SuppressLint;
import android.os.Debug;

import java.lang.reflect.Field;

public class ReflectUtils {
    @SuppressLint("PrivateApi")
    public static String getCallers(final int depth) {
        try {
            return (String) Debug.class.getDeclaredMethod("getCallers", int.class).invoke(null, depth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getField(Class c, Object o, String f) {
        try {
            Field field = c.getDeclaredField(f);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getField(String s, Object o, String f) {
        try {
            Field field = Class.forName(s).getDeclaredField(f);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setField(String s, Object o, String f, Object v) {
        try {
            Field field = Class.forName(s).getDeclaredField(f);
            field.setAccessible(true);
            field.set(o, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
