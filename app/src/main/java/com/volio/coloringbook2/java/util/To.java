package com.volio.coloringbook2.java.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Util class for {@link Toast} API
 */
public class To {

    private static Context context;

    public static void init(Context context) {
        To.context = context;
    }

    public static void show(String str) {
        show(str, Toast.LENGTH_SHORT);
    }

    public static void show(int strId) {
        show(context.getString(strId), Toast.LENGTH_SHORT);
    }

    public static void show(String str, int length) {
        Toast.makeText(context, str, length).show();
    }

    public static void show(int strId, int length) {
        show(context.getString(strId), length);
    }
}
