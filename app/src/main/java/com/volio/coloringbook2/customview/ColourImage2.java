package com.volio.coloringbook2.customview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.volio.coloringbook2.customview.photoview.OnDrawLineListener;
import com.volio.coloringbook2.customview.photoview.ProgressLoading;
import com.volio.coloringbook2.customview.photoview.SizedStack;

import java.util.Stack;



public class ColourImage2 extends ImageView {

    public void clearPoints() {
        undopoints.clear();
        redopoints.clear();
    }

    public enum Model {
        FILLCOLOR,
        FILLGRADUALCOLOR,
        PICKCOLOR,
        DRAW_LINE,
    }

    private Bitmap mBitmap;
    /**
     * ???????
     */
    private int mBorderColor = -1;

    private Stack<Point> mStacks = new Stack<Point>();
    private int mColor = Color.GREEN;
    private int stacksize = 15;
    private Stack<Bitmap> bmstackundo;
    private Stack<Bitmap> bmstackredo;
    private Stack<Point> undopoints;
    private Stack<Point> redopoints;
    private OnRedoUndoListener onRedoUndoListener;
    private AsyncTask loaderTask;

    private Model model = Model.FILLCOLOR;
    private OnColorPickListener onColorPickListener;

    private OnDrawLineListener onDrawLineListener;

    public ColourImage2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStack();
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
        mBitmap = bt.copy(bt.getConfig(), true);
        setImageBitmap(mBitmap);
    }


    public void fillColorToSameArea(int x, int y) {
        try {
            if (mBitmap.getPixel(x, y) != mColor && !isBorderColor(mBitmap.getPixel(x, y)) && mBitmap.getPixel(x, y) != Color.TRANSPARENT) {
//                Bitmap bm = mBitmap;
//                int w = bm.getWidth();
//                int h = bm.getHeight();
//                int[] pixels = new int[w * h];
//                PhotorThread.getInstance().runBackground(new PhotorThread.IBackground() {
//                    @Override
//                    public void doingBackground() {
//                        Log.d(TAG, "doingBackground: ");
//                        try {
////                            pushUndoStack(bm.copy(bm.getConfig(), true));
//                            int pixel = bm.getPixel(x, y);
////                            bm.getPixels(pixels, 0, w, 0, 0, w, h);
//                            fillColor(pixels, w, h, pixel, mColor, x, y);
//                        } catch (Exception e) {
//                            bmstackundo.pop();
//                            Log.d(TAG, "Exception: " + e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        Log.d(TAG, "onCompleted: ");
//                        bm.setPixels(pixels, 0, w, 0, 0, w, h);
//                        setImageDrawable(new BitmapDrawable(getResources(), bm));
//                        if (onRedoUndoListener != null) {
//                            onRedoUndoListener.onRedoUndo(bmstackundo.size(), bmstackredo.size());
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.d(TAG, "onCancel: ");
//
//                    }
//                });

//                ProgressLoading.show(getContext());
                loaderTask = new AsyncTask() {
                    Bitmap bm = mBitmap;

                    @Override
                    protected Object doInBackground(Object[] objects) {

                        //save bm to undo stack
                        try {
                            pushUndoStack(bm.copy(bm.getConfig(), true));
                            int pixel = bm.getPixel((int) objects[0], (int) objects[1]);
                            int w = bm.getWidth();
                            int h = bm.getHeight();
                            //?????bitmap?????????
                            int[] pixels = new int[w * h];
                            bm.getPixels(pixels, 0, w, 0, 0, w, h);
                            //???
                            fillColor(pixels, w, h, pixel, mColor, (int) objects[0], (int) objects[1]);
                            //????????bitmap
                            bm.setPixels(pixels, 0, w, 0, 0, w, h);
                            return true;
                        } catch (Exception e) {
                            bmstackundo.pop();
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        ProgressLoading.DismissDialog();
                        setImageDrawable(new BitmapDrawable(getResources(), bm));
                        if (onRedoUndoListener != null) {
                            onRedoUndoListener.onRedoUndo(bmstackundo.size(), bmstackredo.size());
                        }
                    }
                }.execute(x, y);
            }
        } catch (Exception e) {
            //do nothing.
        }
    }

//    private boolean isBorderColor(int color) {
//        return Color.red(color) < 150 && Color.green(color) < 150 && Color.blue(color) < 150;
//    }

//    private boolean isBorderColor(int color) {
//        return Color.red(color) < 100 && Color.green(color) < 100 && Color.blue(color) < 100 || color == mColor;
//    }

//    private boolean isBorderColor(int color) {
//        return Color.red(color) < 100 && Color.green(color) < 100 && Color.blue(color) < 100 || Color.red(color) == Color.green(color) && Color.red(color) == Color.blue(color) &&
//                Color.green(color) == Color.blue(color) && Color.red(color) < 150 || color == mColor;
//    }

    private boolean isBorderColor(int color) {
        return Color.red(color) == Color.green(color) && Color.red(color) == Color.blue(color) &&
                Color.green(color) == Color.blue(color) && Color.red(color) == 0;
    }


    private void pushUndoStack(Bitmap bm) {
        bmstackundo.push(bm);
        bmstackredo.clear();
    }

    /**
     * @param pixels   像素数组
     * @param w        宽度
     * @param h        高度
     * @param pixel    当前点的颜色
     * @param newColor 填充色
     * @param i        横坐标
     * @param j        纵坐标
     */
    private void fillColor(int[] pixels, int w, int h, int pixel, int newColor, int i, int j) {
        int orginalX = i;
        int orginalY = j;
        mStacks.clear();
        mStacks.push(new Point(i, j));
        while (!mStacks.isEmpty()) {
            if (loaderTask.isCancelled()) {
                break;
            }

            Point seed = mStacks.pop();
            int count = fillLineLeft(pixels, pixel, w, h, newColor, seed.x, seed.y, orginalX, orginalY);
            int left = seed.x - count + 1;
            count = fillLineRight(pixels, pixel, w, h, newColor, seed.x + 1, seed.y, orginalX, orginalY);
            int right = seed.x + count;

            if (seed.y - 1 >= 0)
                findSeedInNewLine(pixels, pixel, w, h, seed.y - 1, left, right);
            if (seed.y + 1 < h)
                findSeedInNewLine(pixels, pixel, w, h, seed.y + 1, left, right);
        }


    }

    /**
     * @param pixels
     * @param pixel
     * @param w
     * @param h
     * @param i
     * @param left
     * @param right
     */
    private void findSeedInNewLine(int[] pixels, int pixel, int w, int h, int i, int left, int right) {
        int begin = i * w + left;
        int end = i * w + right;
        boolean hasSeed = false;
        int rx, ry;
        ry = i;
        while (end >= begin) {

            if (needFillPixel(pixels, pixel, end)) {
                if (!hasSeed) {
                    rx = end % w;
                    mStacks.push(new Point(rx, ry));
                    hasSeed = true;
                }
            } else {
                hasSeed = false;
            }
            end--;
        }
    }


    /**
     * ???????????????????????
     *
     * @return
     */


    private static final String TAG = "dsk";

    private int fillLineLeft(int[] pixels, int pixel, int w, int h, int newColor, int x, int y, int orginalX, int orginalY) {
        int count = 0;
        while (x >= 0) {
            //?????????
            int index = y * w + x;

            if (needFillPixel(pixels, pixel, index)) {
                if (model == Model.FILLCOLOR) {
                    pixels[index] = newColor;
                } else if (model == Model.FILLGRADUALCOLOR) {
                    float[] colorHSV = new float[]{0f, 0f, 1f};
                    Color.colorToHSV(newColor, colorHSV);
                    float dis = (float) Math.sqrt((x - orginalX) * (x - orginalX) + (y - orginalY) * (y - orginalY));
                    colorHSV[1] = (colorHSV[1] - dis * 0.006) < 0.2 ? 0.2f : (colorHSV[1] - dis * 0.006f);
                    pixels[index] = Color.HSVToColor(colorHSV);
                }
                count++;
                x--;
            } else {
                break;
            }

        }
        return count;
    }

    private int fillLineRight(int[] pixels, int pixel, int w, int h, int newColor, int x, int y, int orginalX, int orginalY) {
        int count = 0;

        while (x < w) {
            //???????
            int index = y * w + x;
            if (needFillPixel(pixels, pixel, index)) {
                if (model == Model.FILLCOLOR) {
                    pixels[index] = newColor;
                } else if (model == Model.FILLGRADUALCOLOR) {
                    float[] colorHSV = new float[]{0f, 0f, 1f};
                    Color.colorToHSV(newColor, colorHSV);
                    float dis = (float) Math.sqrt((x - orginalX) * (x - orginalX) + (y - orginalY) * (y - orginalY));
                    colorHSV[1] = (colorHSV[1] - dis * 0.006) < 0.2 ? 0.2f : (colorHSV[1] - dis * 0.006f);
                    pixels[index] = Color.HSVToColor(colorHSV);
                }
                count++;
                x++;
            } else {
                break;
            }

        }
        return count;
    }

    private boolean needFillPixel(int[] pixels, int pixel, int index) {
//        if (model == Model.FILLGRADUALCOLOR) {
//            return pixels[index] == pixel;
//        } else
//            return pixels[index] == pixel;
//        return isBlack(pixels[index]) || pixels[index] == pixel;
        return pixels[index] == pixel;

    }

    private boolean isBlack(int i) {
        return Color.red(i) > whiteColor && Color.green(i) > whiteColor && Color.blue(i) > whiteColor;
    }

    public int whiteColor = 200;


    public void setImageBT(Bitmap bm) {
        pushUndoStack(mBitmap.copy(mBitmap.getConfig(), true));
        mBitmap = bm.copy(bm.getConfig(), true);
        setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
        if (onRedoUndoListener != null) {
            onRedoUndoListener.onRedoUndo(bmstackundo.size(), bmstackredo.size());
        }
    }

    public void update() {
        setMeasuredDimension(getMeasuredWidth(),
                getDrawable().getIntrinsicHeight() * getMeasuredWidth() / getDrawable().getIntrinsicWidth());
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setColor(int color) {
        mColor = color;
    }

    /**
     * @return true: has element can undo;
     */
    public boolean undo() {
        try {
            if (bmstackundo.peek() != null) {
                bmstackredo.push(mBitmap.copy(mBitmap.getConfig(), true));
                mBitmap = bmstackundo.pop();
                setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
                if (onRedoUndoListener != null) {
                    onRedoUndoListener.onRedoUndo(bmstackundo.size(), bmstackredo.size());
                }
                if (undopoints != null && !undopoints.empty()) {
                    redopoints.push(undopoints.pop());
                }
                return !bmstackundo.empty();
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * @return true: has element ,can redo;
     */
    public boolean redo() {
        try {
            if (bmstackredo.peek() != null) {
                bmstackundo.push(mBitmap.copy(mBitmap.getConfig(), true));
                mBitmap = bmstackredo.pop();
                setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
                if (onRedoUndoListener != null) {
                    onRedoUndoListener.onRedoUndo(bmstackundo.size(), bmstackredo.size());
                }
                if (redopoints != null && !redopoints.empty()) {
                    undopoints.push(redopoints.pop());
                }
                return !bmstackredo.empty();
            }
        } catch (Exception e) {

        }
        return false;
    }

    public OnRedoUndoListener getOnRedoUndoListener() {
        return onRedoUndoListener;
    }

    public void setOnRedoUndoListener(OnRedoUndoListener onRedoUndoListener) {
        this.onRedoUndoListener = onRedoUndoListener;
    }

    //clear stack and the current image
    public void clearStack() {
        bmstackredo.clear();
        bmstackundo.clear();
        onRedoUndoListener.onRedoUndo(bmstackundo.size(), bmstackredo.size());
        mBitmap = null;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setOnColorPickListener(OnColorPickListener onColorPickListener) {
        this.onColorPickListener = onColorPickListener;
    }

    public interface OnRedoUndoListener {
        void onRedoUndo(int undoSize, int redoSize);
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    private void initStack() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Cache", Context.MODE_PRIVATE);
        stacksize = sharedPreferences.getInt("stack_max_size", 10);
        bmstackundo = new SizedStack<>(stacksize);
        bmstackredo = new SizedStack<>(stacksize);
        undopoints = new Stack<>();
        redopoints = new Stack<>();
    }

    public interface OnColorPickListener {
        void onColorPick(boolean status, int color);
    }

    /**
     * call only for activity destroyed
     */
    public void onRecycleBitmaps() {
        while (bmstackundo != null && !bmstackundo.empty()) {
            bmstackundo.pop().recycle();
            bmstackundo.clear();
        }

        while (bmstackredo != null && !bmstackredo.empty()) {
            bmstackredo.pop().recycle();
            bmstackredo.clear();
        }
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

    public void drawLine(int x, int y) {
        if (undopoints != null && !undopoints.empty()) {
            drawBlackLine(undopoints.peek().x, undopoints.peek().y, x, y);
            undopoints.push(new Point(x, y));
            if (onDrawLineListener != null)
                onDrawLineListener.OnGivenNextPointListener(x, y);
        } else {
            undopoints.push(new Point(x, y));
            if (onDrawLineListener != null)
                onDrawLineListener.OnGivenFirstPointListener(x, y);
        }
    }

    private void drawBlackLine(int startX, int startY, int endX, int endY) {
        try {
            Log.e("draw", startX + "," + startY + "," + endX + "," + endY);
            Bitmap bm = mBitmap;
            //format points
            startX = startX >= bm.getWidth() ? bm.getWidth() - 1 : startX;
            startX = startX < 0 ? 0 : startX;
            startY = startY >= bm.getHeight() ? bm.getHeight() - 1 : startY;
            startY = startY < 0 ? 0 : startY;
            endX = endX >= bm.getWidth() ? bm.getWidth() - 1 : endX;
            endX = endX < 0 ? 0 : endX;
            endY = endY >= bm.getHeight() ? bm.getHeight() - 1 : endY;
            endY = endY < 0 ? 0 : endY;
            //test points
            bm.getPixel(endX, endY);
            bm.getPixel(startX, startY);
            pushUndoStack(bm.copy(bm.getConfig(), true));
            doingDrawLine(bm, startX, startY, endX, endY);
            setImageDrawable(new BitmapDrawable(getResources(), bm));
            if (onRedoUndoListener != null) {
                onRedoUndoListener.onRedoUndo(bmstackundo.size(), bmstackredo.size());
            }
            if (onDrawLineListener != null)
                onDrawLineListener.OnDrawFinishedListener(true, startX, startY, endX, endY);
        } catch (Exception e) {
            Log.e("drawline", e.toString());
            bmstackundo.pop();
            if (onDrawLineListener != null)
                onDrawLineListener.OnDrawFinishedListener(false, startX, startY, endX, endY);
        }

    }

    private void doingDrawLine(Bitmap bm, int startX, int startY, int endX, int endY) {
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStrokeWidth(2);
        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    private void doingDrawLineAsyn(Bitmap bm, int startX, int startY, int endX, int endY) {
        //if two point same reture
        if (startX == endX && startY == endY) {
            bm.setPixel(startX, startY, 0xFF000000);
            return;
        }
        //if shuxian
        if (startX == endX) {
            if (endY > startY) {
                for (int i = startY; i < endY; i++) {
                    bm.setPixel(startX, i, 0xFF000000);
                }
            } else {
                for (int i = endY; i < startY; i++) {
                    bm.setPixel(startX, i, 0xFF000000);
                }
            }
            return;
        }
        //if henxian
        if (startY == endY) {
            if (endX > startX) {
                for (int i = startX; i < endX; i++) {
                    bm.setPixel(i, startY, 0xFF000000);
                }
            } else {
                for (int i = endX; i < startX; i++) {
                    bm.setPixel(i, startY, 0xFF000000);
                }
            }
            return;
        }
        //if xiexian
        if (Math.abs(endY - startY) > Math.abs(endX - startX)) {
            float radio = Math.abs((float) (endY - startY) / (endX - startX));
            int offset = 0;
            int bushu = radio % 1 == 0 ? 0 : (int) (1 / (radio % 1));
            int tempY;
            if (endY > startY && endX > startX) {
                tempY = startY;
                for (int i = startX; i < endX; i++) {
                    for (int j = 0; j <= radio; j++) {
                        bm.setPixel(i, tempY + j, 0xFF000000);
                    }
                    tempY += (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(i, tempY++, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            } else if (endY < startY && endX > startX) {
                tempY = startY;
                for (int i = startX; i < endX; i++) {
                    for (int j = 0; j <= radio; j++) {
                        bm.setPixel(i, tempY - j, 0xFF000000);
                    }
                    tempY -= (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(i, tempY--, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            } else if (endY > startY && endX < startX) {

                tempY = endY;
                for (int i = endX; i < startX; i++) {
                    for (int j = 0; j <= radio; j++) {
                        bm.setPixel(i, tempY - j, 0xFF000000);
                    }
                    tempY -= (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(i, tempY--, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            } else if (endY < startY && endX < startX) {
                tempY = endY;
                for (int i = endX; i < startX; i++) {
                    for (int j = 0; j <= radio; j++) {
                        bm.setPixel(i, tempY + j, 0xFF000000);
                    }
                    tempY += (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(i, tempY++, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            }
        } else {
            float radio = Math.abs((float) (endX - startX) / (endY - startY));
            int offset = 0;
            int bushu = radio % 1 == 0 ? 0 : (int) (1 / (radio % 1));
            int tempX;
            if (endY > startY && endX > startX) {
                tempX = startX; //select small one
                for (int i = startY; i < endY; i++) { //loop start at small one end at large one
                    for (int j = 0; j <= radio; j++) {
                        bm.setPixel(tempX + j, i, 0xFF000000);
                    }
                    tempX += (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(++tempX, i, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            } else if (endY < startY && endX > startX) {
                tempX = endX;
                for (int i = endY; i < startY; i++) {
                    for (int j = 0; j <= radio; j++) {
                        bm.setPixel(tempX - j, i, 0xFF000000);
                    }
                    tempX -= (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(--tempX, i, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            } else if (endY > startY && endX < startX) {
                tempX = startX;
                for (int i = startY; i < endY; i++) {
                    for (int j = 0; j <= radio; j++) {
                        bm.setPixel(tempX - j, i, 0xFF000000);
                    }
                    tempX -= (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(--tempX, i, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            } else if (endY < startY && endX < startX) {
                tempX = endX;
                for (int i = endY; i < startY; i++) {
                    for (int j = 0; j < radio; j++) {
                        bm.setPixel(tempX + j, i, 0xFF000000);
                    }
                    tempX += (int) radio;
                    if (bushu != 0) {
                        if (offset == bushu) {
                            bm.setPixel(++tempX, i, 0xFF000000);
                            offset = 0;
                        } else {
                            offset++;
                        }
                    }
                }
            }
        }
    }

    public void setOnDrawLineListener(OnDrawLineListener onDrawLineListener) {
        this.onDrawLineListener = onDrawLineListener;
    }
}