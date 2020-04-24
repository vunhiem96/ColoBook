package com.volio.coloringbook2.java.util;

import android.view.MotionEvent;
import android.view.View;

public interface OnCustomTouchListener {
    void OnCustomTouchDown(View v, MotionEvent event);

    void OnCustomTouchMoveOut(View v, MotionEvent event);

    void OnCustomTouchUp(View v, MotionEvent event);

    void OnCustomTouchOther(View v, MotionEvent event);
}
