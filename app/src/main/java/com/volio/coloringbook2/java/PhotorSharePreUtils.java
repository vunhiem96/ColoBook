package com.volio.coloringbook2.java;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PhotorSharePreUtils {

    private static SharedPreferences mSharePref;

    public static void init(Context context) {
        if (mSharePref == null)
            mSharePref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void clearAll() {
        mSharePref.edit().clear().apply();
        mSharePref.edit().commit();
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = mSharePref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, String defaultValues) {
        return mSharePref.getString(key, defaultValues);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = mSharePref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defaultValues) {
        return mSharePref.getInt(key, defaultValues);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = mSharePref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defaultValues) {
        return mSharePref.getLong(key, defaultValues);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharePref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defaultValues) {
        return mSharePref.getBoolean(key, defaultValues);
    }
}
