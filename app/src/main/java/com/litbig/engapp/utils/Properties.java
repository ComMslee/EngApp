package com.litbig.engapp.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class Properties {
    private static final String TAG = "Properties";

    public static void set(@NotNull String key, @NotNull String val) throws IllegalArgumentException {

        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = {String.class, String.class};
            Method set = SystemProperties.getMethod("set", paramTypes);

            //Parameters
            Object[] params = {key, val};
            set.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "IllegalArgumentException e: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "IllegalArgumentException e: " + e.toString());
        }

    }

    public static String get(@NotNull String key) {

        String ret = "";
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = {String.class};
            Method get = SystemProperties.getMethod("get", paramTypes);

            //Parameters
            Object[] params = {key};
            ret = (String) get.invoke(SystemProperties, params);

        } catch (IllegalArgumentException e) {
            ret = "";
            e.printStackTrace();
            Log.e(TAG, "IllegalArgumentException e: " + e.toString());
        } catch (Exception e) {
            ret = "";
            e.printStackTrace();
            Log.e(TAG, "Exception e: " + e.toString());
        }

        return ret;

    }
}
