package com.volio.coloringbook2.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

public class OneOneFrameLayout extends FrameLayout {

    public OneOneFrameLayout(@NonNull Context context) {
        super(context);
    }

    public OneOneFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OneOneFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OneOneFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int snHeight = MeasureSpec.getSize(widthMeasureSpec);
        int snHeightSpec = MeasureSpec.makeMeasureSpec(snHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, snHeightSpec);
    }
}