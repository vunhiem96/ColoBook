package com.volio.coloringbook2.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OneOneRelativeLayout extends RelativeLayout {

    public OneOneRelativeLayout(@NonNull Context context) {
        super(context);
    }

    public OneOneRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OneOneRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public OneOneRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int snHeight = MeasureSpec.getSize(widthMeasureSpec);
        int snHeightSpec = MeasureSpec.makeMeasureSpec(snHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, snHeightSpec);
    }
}