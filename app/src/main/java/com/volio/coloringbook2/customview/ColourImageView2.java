package com.volio.coloringbook2.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.volio.coloringbook2.java.DrawModel;
import com.volio.coloringbook2.java.PhotorThread;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;


public class ColourImageView2 extends ImageView {


    private Bitmap mBitmap;


    private Stack<Point> mStacks = new Stack<Point>();
    private int mColor = Color.GREEN;

    public OnRedoUndoListener onRedoUndoListener;
    private ArrayList<DrawModel> list = new ArrayList<>();
    private int current_step = 0;
    private int total_step = 0;

    int undoSize = 0;
    int reduSize = 0;


    public ColourImageView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public Bitmap createBitMap() {
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
        Bitmap bm = drawable.getBitmap();
        return bm.copy(bm.getConfig(), true);
    }

    public void createBitMap(Bitmap bt) {
        try {
            mBitmap = bt.copy(bt.getConfig(), true);
            setImageBitmap(mBitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }


    public void fillColorToSameArea(int i2, int i3) {
        try {
            if (!isBorderColor(this.mBitmap.getPixel(i2, i3))) {
                actionFillColor(i2, i3, mColor, true);
                undoSize = 1;
                if (onRedoUndoListener != null) onRedoUndoListener.onRedoUndo(undoSize, reduSize);
            }
        } catch (Exception ignored) {
        }
    }


    private void actionFillColor(int i2, int i3, int mColor, boolean isTouch) {
        if (mColor != mBitmap.getPixel(i2, i3)) {
            PhotorThread.getInstance().runBackground(new PhotorThread.IBackground() {
                @Override
                public void doingBackground() {
                    int pixel = mBitmap.getPixel(i2, i3);
                    int width = mBitmap.getWidth();
                    int height = mBitmap.getHeight();
                    int[] iArr = new int[(width * height)];
                    if (isTouch) {
                        Point point = new Point(i2, i3);
                        DrawModel model = new DrawModel(point, pixel, mColor);
                        if (current_step < total_step) {
                            int i = total_step - 1;
                            while (i >= current_step) {
                                try {
                                    list.remove(i);
                                } catch (IndexOutOfBoundsException e) {
                                }
                                i--;
                            }
                            current_step++;
                            total_step = current_step;
                        } else {
                            total_step++;
                            current_step = total_step;
                        }
                        list.add(model);
                    }

                    mBitmap.getPixels(iArr, 0, width, 0, 0, width, height);
                    try {
                        a(iArr, width, height, pixel, mColor, i2, i3);
                        mBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCompleted() {
                    setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

    public void actionBackStep() {
        if (current_step > 0) {
            Log.d(TAG, "current_step: " + current_step);
            if (current_step - 1 < list.size()) {
                DrawModel model = list.get(current_step - 1);
                current_step--;
                actionFillColor(model.getPoint().x, model.getPoint().y, model.getSourceColor(), false);
                if (current_step == 0) {
                    undoSize = 0;
                }
                reduSize = 1;
                if (onRedoUndoListener != null) onRedoUndoListener.onRedoUndo(undoSize, reduSize);
            }
        }
    }

    public void actionForwardStep() {
        if (current_step < total_step) {
            DrawModel model = list.get(current_step);
            actionFillColor(model.getPoint().x, model.getPoint().y, model.getTargetColor(), false);
            current_step++;
            if (current_step == total_step) {
                reduSize = 0;
            }
            undoSize = 1;
            if (onRedoUndoListener != null) onRedoUndoListener.onRedoUndo(undoSize, reduSize);
        }
    }


    public void a(int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7) {
        this.mStacks.clear();
        this.mStacks.push(new Point(i6, i7));


        while (!this.mStacks.isEmpty()) {
            try {
                Point point = this.mStacks.pop();
                int a2 = (point.x - a(iArr, i4, i2, i3, i5, point.x, point.y, i6, i7)) + 1;
                int b2 = point.x + b(iArr, i4, i2, i3, i5, point.x + 1, point.y, i6, i7);
                if (point.y - 1 >= 0) {
                    b(iArr, i4, i2, i3, point.y - 1, a2, b2);
                }
                if (point.y + 1 < i3) {
                    b(iArr, i4, i2, i3, point.y + 1, a2, b2);
                }
            } catch (EmptyStackException e) {
                e.printStackTrace();
            }
        }
    }

    private void b(int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7) {
        int i8 = i5 * i3;
        int i9 = i6 + i8;
        boolean z = false;
        for (int i10 = i8 + i7; i10 >= i9; i10--) {
            if (!a(iArr, i2, i10)) {
                z = false;
            } else if (!z) {
                try {
                    this.mStacks.push(new Point(i10 % i3, i5));
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                z = true;
            }
        }
    }


    private int b(int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        int i12 = 0;
        for (int i13 = i6; i13 < i3; i13++) {
            int i14 = (i7 * i3) + i13;
            if (!a(iArr, i2, i14)) {
                break;
            }
            iArr[i14] = i5;
            i12++;
        }
        return i12;
    }

    private int a(int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        int i11 = 0;
        for (int i12 = i6; i12 >= 0; i12--) {
            int i13 = (i7 * i3) + i12;
            if (!a(iArr, i2, i13)) {
                break;
            }
            iArr[i13] = i5;
            i11++;
        }
        return i11;
    }

    private boolean a(int[] iArr, int i2, int i3) {
        boolean z = false;
        if (iArr[i3] == i2) {
            z = true;
        }
        return z;
    }

    private boolean isBorderColor(int color) {
        return Color.red(color) == Color.green(color) && Color.red(color) == Color.blue(color) &&
                Color.green(color) == Color.blue(color) && Color.red(color) == 0;
    }




    private static final String TAG = "dsk";


    public void update() {
        if (getDrawable() != null) {
            setMeasuredDimension(getMeasuredWidth(),
                    getDrawable().getIntrinsicHeight() * getMeasuredWidth() / getDrawable().getIntrinsicWidth());
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setColor(int color) {
        mColor = color;
    }



    public interface OnRedoUndoListener {
        void onRedoUndo(int undoSize, int redoSize);
    }


}


