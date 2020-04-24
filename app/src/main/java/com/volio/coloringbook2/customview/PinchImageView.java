package com.volio.coloringbook2.customview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 手势图片控件
 *
 * @author clifford
 */
public class PinchImageView extends AppCompatImageView {


    public static final int SCALE_ANIMATOR_DURATION = 200;

    public static final float FLING_DAMPING_FACTOR = 0.9f;

    private static final float MAX_SCALE = 4f;

    private OnClickListener mOnClickListener;

    private OnLongClickListener mOnLongClickListener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mOnLongClickListener = l;
    }

    public static final int PINCH_MODE_FREE = 0;

    public static final int PINCH_MODE_SCROLL = 1;

    public static final int PINCH_MODE_SCALE = 2;

    private Matrix mOuterMatrix = new Matrix();

    private RectF mMask;


    //vinh
    private Bitmap mBitmap;
    int[] f2121p;
    ArrayList<Point> pts = new ArrayList<>();
    String Drag = "none";
    public boolean isFillModeEnabled = true;
    public int imageWidth;
    public int imageHeight;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mScaleFactor = 1.f;
    private float mLastTouchX;
    private float mLastTouchY;
    public float mPosX = 1;
    public float mPosY = 1;
    Rect rect;
    private Point p1 = new Point();
    private Canvas canvas;
    Paint paint;


    private int mPinchMode = PINCH_MODE_FREE;

    public Matrix getOuterMatrix(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix(mOuterMatrix);
        } else {
            matrix.set(mOuterMatrix);
        }
        return matrix;
    }

    public Matrix getInnerMatrix(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            matrix.reset();
        }
        if (isReady()) {
            RectF tempSrc = MathUtils.rectFTake(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());

            RectF tempDst = MathUtils.rectFTake(0, 0, getWidth(), getHeight());

            matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);

            MathUtils.rectFGiven(tempDst);
            MathUtils.rectFGiven(tempSrc);
        }
        return matrix;
    }

    public Matrix getCurrentImageMatrix(Matrix matrix) {
        matrix = getInnerMatrix(matrix);
        matrix.postConcat(mOuterMatrix);
        return matrix;
    }


    public RectF getImageBound(RectF rectF) {
        if (rectF == null) {
            rectF = new RectF();
        } else {
            rectF.setEmpty();
        }
        if (!isReady()) {
            return rectF;
        } else {
            Matrix matrix = MathUtils.matrixTake();
            getCurrentImageMatrix(matrix);
            rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            matrix.mapRect(rectF);
            MathUtils.matrixGiven(matrix);
            return rectF;
        }
    }

    public RectF getMask() {
        if (mMask != null) {
            return new RectF(mMask);
        } else {
            return null;
        }
    }

    public int getPinchMode() {
        return mPinchMode;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (mPinchMode == PinchImageView.PINCH_MODE_SCALE) {
            return true;
        }
        RectF bound = getImageBound(null);
        if (bound == null) {
            return false;
        }
        if (bound.isEmpty()) {
            return false;
        }
        if (direction > 0) {
            return bound.right > getWidth();
        } else {
            return bound.left < 0;
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (mPinchMode == PinchImageView.PINCH_MODE_SCALE) {
            return true;
        }
        RectF bound = getImageBound(null);
        if (bound == null) {
            return false;
        }
        if (bound.isEmpty()) {
            return false;
        }
        if (direction > 0) {
            return bound.bottom > getHeight();
        } else {
            return bound.top < 0;
        }
    }

    public void outerMatrixTo(Matrix endMatrix, long duration) {
        if (endMatrix == null) {
            return;
        }
        mPinchMode = PINCH_MODE_FREE;
        cancelAllAnimator();
        if (duration <= 0) {
            mOuterMatrix.set(endMatrix);
            dispatchOuterMatrixChanged();
            invalidate();
        } else {
            mScaleAnimator = new ScaleAnimator(mOuterMatrix, endMatrix, duration);
            mScaleAnimator.start();
        }
    }

    public void zoomMaskTo(RectF mask, long duration) {
        if (mask == null) {
            return;
        }
        if (mMaskAnimator != null) {
            mMaskAnimator.cancel();
            mMaskAnimator = null;
        }
        if (duration <= 0 || mMask == null) {
            if (mMask == null) {
                mMask = new RectF();
            }
            mMask.set(mask);
            invalidate();
        } else {
            mMaskAnimator = new MaskAnimator(mMask, mask, duration);
            mMaskAnimator.start();
        }
    }

    public void reset() {
        mOuterMatrix.reset();
        dispatchOuterMatrixChanged();
        mMask = null;
        mPinchMode = PINCH_MODE_FREE;
        mLastMovePoint.set(0, 0);
        mScaleCenter.set(0, 0);
        mScaleBase = 0;
        if (mMaskAnimator != null) {
            mMaskAnimator.cancel();
            mMaskAnimator = null;
        }
        cancelAllAnimator();
        invalidate();
    }


    public interface OuterMatrixChangedListener {

        void onOuterMatrixChanged(PinchImageView pinchImageView);
    }

    private List<OuterMatrixChangedListener> mOuterMatrixChangedListeners;

    private List<OuterMatrixChangedListener> mOuterMatrixChangedListenersCopy;

    private int mDispatchOuterMatrixChangedLock;

    public void addOuterMatrixChangedListener(OuterMatrixChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListeners == null) {
                mOuterMatrixChangedListeners = new ArrayList<OuterMatrixChangedListener>();
            }
            mOuterMatrixChangedListeners.add(listener);
        } else {
            if (mOuterMatrixChangedListenersCopy == null) {
                if (mOuterMatrixChangedListeners != null) {
                    mOuterMatrixChangedListenersCopy = new ArrayList<OuterMatrixChangedListener>(mOuterMatrixChangedListeners);
                } else {
                    mOuterMatrixChangedListenersCopy = new ArrayList<OuterMatrixChangedListener>();
                }
            }
            mOuterMatrixChangedListenersCopy.add(listener);
        }
    }

    public void removeOuterMatrixChangedListener(OuterMatrixChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListeners != null) {
                mOuterMatrixChangedListeners.remove(listener);
            }
        } else {
            if (mOuterMatrixChangedListenersCopy == null) {
                if (mOuterMatrixChangedListeners != null) {
                    mOuterMatrixChangedListenersCopy = new ArrayList<OuterMatrixChangedListener>(mOuterMatrixChangedListeners);
                }
            }
            if (mOuterMatrixChangedListenersCopy != null) {
                mOuterMatrixChangedListenersCopy.remove(listener);
            }
        }
    }

    private void dispatchOuterMatrixChanged() {
        if (mOuterMatrixChangedListeners == null) {
            return;
        }
        mDispatchOuterMatrixChangedLock++;
        for (OuterMatrixChangedListener listener : mOuterMatrixChangedListeners) {
            listener.onOuterMatrixChanged(this);
        }
        mDispatchOuterMatrixChangedLock--;
        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListenersCopy != null) {
                //将副本替换掉正式的列表
                mOuterMatrixChangedListeners = mOuterMatrixChangedListenersCopy;
                //清空副本
                mOuterMatrixChangedListenersCopy = null;
            }
        }
    }


    protected float getMaxScale() {
        return MAX_SCALE;
    }

    protected float calculateNextScale(float innerScale, float outerScale) {
        float currentScale = innerScale * outerScale;
        if (currentScale < MAX_SCALE) {
            return MAX_SCALE;
        } else {
            return innerScale;
        }
    }


    public PinchImageView(Context context) {
        super(context);
        initView();
    }

    public PinchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PinchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {

        super.setScaleType(ScaleType.MATRIX);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#a6f914"));
        paint.setAntiAlias(true);
    }

    //不允许设置scaleType，只能用内部设置的matrix
    @Override
    public void setScaleType(ScaleType scaleType) {
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isReady()) {
            Matrix matrix = MathUtils.matrixTake();
            setImageMatrix(getCurrentImageMatrix(matrix));
            MathUtils.matrixGiven(matrix);
        }

        if (mMask != null) {
            canvas.save();
            canvas.clipRect(mMask);
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
        canvas.drawPaint(paint);

//        if (drawn)
//            canvas.drawBitmap(mBitmap, 0, 0, paint);
    }

    Boolean drawn = false;


    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    private boolean isReady() {
        return getDrawable() != null && getDrawable().getIntrinsicWidth() > 0 && getDrawable().getIntrinsicHeight() > 0
                && getWidth() > 0 && getHeight() > 0;
    }


    private MaskAnimator mMaskAnimator;

    /**
     * mask变换动画
     * <p>
     * 将mask从一个rect动画到另外一个rect
     */
    private class MaskAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        /**
         * 开始mask
         */
        private float[] mStart = new float[4];

        /**
         * 结束mask
         */
        private float[] mEnd = new float[4];

        /**
         * 中间结果mask
         */
        private float[] mResult = new float[4];

        /**
         * 创建mask变换动画
         *
         * @param start    动画起始状态
         * @param end      动画终点状态
         * @param duration 动画持续时间
         */
        public MaskAnimator(RectF start, RectF end, long duration) {
            super();
            setFloatValues(0, 1f);
            setDuration(duration);
            addUpdateListener(this);
            //将起点终点拷贝到数组方便计算
            mStart[0] = start.left;
            mStart[1] = start.top;
            mStart[2] = start.right;
            mStart[3] = start.bottom;
            mEnd[0] = end.left;
            mEnd[1] = end.top;
            mEnd[2] = end.right;
            mEnd[3] = end.bottom;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //获取动画进度,0-1范围
            float value = (Float) animation.getAnimatedValue();
            //根据进度对起点终点之间做插值
            for (int i = 0; i < 4; i++) {
                mResult[i] = mStart[i] + (mEnd[i] - mStart[i]) * value;
            }
            //期间mask有可能被置空了,所以判断一下
            if (mMask == null) {
                mMask = new RectF();
            }
            //设置新的mask并绘制
            mMask.set(mResult[0], mResult[1], mResult[2], mResult[3]);
            invalidate();
        }
    }


    ////////////////////////////////手势动画处理////////////////////////////////

    /**
     * 在单指模式下:
     * 记录上一次手指的位置,用于计算新的位置和上一次位置的差值.
     * <p>
     * 双指模式下:
     * 记录两个手指的中点,作为和mScaleCenter绑定的点.
     * 这个绑定可以保证mScaleCenter无论如何都会跟随这个中点.
     *
     * @see #mScaleCenter
     * @see #scale(PointF, float, float, PointF)
     * @see #scaleEnd()
     */
    private PointF mLastMovePoint = new PointF();

    /**
     * 缩放模式下图片的缩放中点.
     * <p>
     * 为其指代的点经过innerMatrix变换之后的值.
     * 其指代的点在手势过程中始终跟随mLastMovePoint.
     * 通过双指缩放时,其为缩放中心点.
     *
     * @see #saveScaleContext(float, float, float, float)
     * @see #mLastMovePoint
     * @see #scale(PointF, float, float, PointF)
     */
    private PointF mScaleCenter = new PointF();

    /**
     * 缩放模式下的基础缩放比例
     * <p>
     * 为外层缩放值除以开始缩放时两指距离.
     * 其值乘上最新的两指之间距离为最新的图片缩放比例.
     *
     * @see #saveScaleContext(float, float, float, float)
     * @see #scale(PointF, float, float, PointF)
     */
    private float mScaleBase = 0;

    /**
     * 图片缩放动画
     * <p>
     * 缩放模式把图片的位置大小超出限制之后触发.
     * 双击图片放大或缩小时触发.
     * 手动调用outerMatrixTo触发.
     *
     * @see #scaleEnd()
     * @see #doubleTap(float, float)
     * @see #outerMatrixTo(Matrix, long)
     */
    private ScaleAnimator mScaleAnimator;

    /**
     * 滑动产生的惯性动画
     *
     * @see #fling(float, float)
     */
    private FlingAnimator mFlingAnimator;

    /**
     * 常用手势处理
     * <p>
     * 在onTouchEvent末尾被执行.
     */
    private GestureDetector mGestureDetector = new GestureDetector(PinchImageView.this.getContext(), new GestureDetector.SimpleOnGestureListener() {

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //只有在单指模式结束之后才允许执行fling
            if (mPinchMode == PINCH_MODE_FREE && !(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                fling(velocityX, velocityY);
            }
            return true;
        }

        public void onLongPress(MotionEvent e) {
            //触发长按
            if (mOnLongClickListener != null) {
                mOnLongClickListener.onLongClick(PinchImageView.this);
            }
        }

        public boolean onDoubleTap(MotionEvent e) {
            //当手指快速第二次按下触发,此时必须是单指模式才允许执行doubleTap
            if (mPinchMode == PINCH_MODE_SCROLL && !(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                doubleTap(e.getX(), e.getY());
            }
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            //触发点击
            if (mOnClickListener != null) {
                mOnClickListener.onClick(PinchImageView.this);
            }
            return true;
        }
    });

    class TheTask extends AsyncTask<Void, Integer, Void> {

        Bitmap bmp;
        Point pt;
        int replacementColor, targetColor;


        TheTask(Bitmap bm, Point p, int sc, int tc) {
            this.bmp = bm;
            this.pt = p;
            this.replacementColor = tc;
            this.targetColor = sc;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... params) {
            floodFill(bmp, pt, targetColor, replacementColor);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            invalidate();
        }
    }

    private void floodFill(Bitmap bitmap, Point point, int i, int j) {
        int k = bitmap.getWidth();
        int l = bitmap.getHeight();

        pts.clear();
        if (i == j) {
            j = Color.WHITE;
        }
        if (i != j) {
            LinkedList linkedlist = new LinkedList();

            do {

                int i1 = point.x;
                int j1;
                for (j1 = point.y; i1 > 0 && !isBlack(bitmap.getPixel(i1 - 1, j1), j); i1--) {
                }
                boolean flag = false;
                boolean flag1 = false;
                while (i1 < k && !isBlack(bitmap.getPixel(i1, j1), j)) {//
                    bitmap.setPixel(i1, j1, j);

                    if (!flag && j1 > 0 && !isBlack(bitmap.getPixel(i1, j1 - 1), j)) {
                        linkedlist.add(new Point(i1, j1 - 1));

                        flag = true;
                    } else if (flag && j1 > 0 && isBlack(bitmap.getPixel(i1, j1 - 1), j)) {
                        flag = false;
                    }
                    if (!flag1 && j1 < l - 1 && !isBlack(bitmap.getPixel(i1, j1 + 1), j)) {


                        linkedlist.add(new Point(i1, j1 + 1));
                        flag1 = true;
                    } else if (flag1 && j1 < l - 1 && isBlack(bitmap.getPixel(i1, j1 + 1), j)) {
                        flag1 = false;
                    }
                    i1++;
                }
                point = (Point) linkedlist.poll();
                pts.add(point);
            } while (point != null);


        }
    }

    private boolean isBlack(int i, int j) {
        while (Color.red(i) == Color.green(i) && Color.green(i) == Color.blue(i) && Color.red(i) < 150 || i == j) {
            return true;
        }
        return false;
    }






    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        //最后一个点抬起或者取消，结束所有模式
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            //如果之前是缩放模式,还需要触发一下缩放结束动画
            if (mPinchMode == PINCH_MODE_SCALE) {
                scaleEnd();
            }
            mPinchMode = PINCH_MODE_FREE;

            //vinh
            mActivePointerId = INVALID_POINTER_ID;

            float mx = event.getX() / mScaleFactor + rect.left;
            float my = event.getY() / mScaleFactor + rect.top;
            rect = canvas.getClipBounds();
            Log.e("MotionEvent", "==========ACTION_UP===" + mx + "====" + my);
            p1.x = (int) mx;
            p1.y = (int) my;
            if (isFillModeEnabled) {
                if (mBitmap.getHeight() > my && mBitmap.getWidth() > mx) {
                    if (my >= 0 && mx >= 0) {
                        final int sourceColor = mBitmap.getPixel((int) mx, (int) my);
                        int targetColor = paint.getColor();
                        new TheTask(mBitmap, p1, sourceColor, targetColor).execute();
                        invalidate();
                    }
                }

            }
            Drag = "none";



        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            //多个手指情况下抬起一个手指,此时需要是缩放模式才触发
            if (mPinchMode == PINCH_MODE_SCALE) {
                //抬起的点如果大于2，那么缩放模式还有效，但是有可能初始点变了，重新测量初始点
                if (event.getPointerCount() > 2) {
                    //如果还没结束缩放模式，但是第一个点抬起了，那么让第二个点和第三个点作为缩放控制点
                    if (event.getAction() >> 8 == 0) {
                        saveScaleContext(event.getX(1), event.getY(1), event.getX(2), event.getY(2));
                        //如果还没结束缩放模式，但是第二个点抬起了，那么让第一个点和第三个点作为缩放控制点
                    } else if (event.getAction() >> 8 == 1) {
                        saveScaleContext(event.getX(0), event.getY(0), event.getX(2), event.getY(2));
                    }
                }
                //如果抬起的点等于2,那么此时只剩下一个点,也不允许进入单指模式,因为此时可能图片没有在正确的位置上
            }
            //第一个点按下，开启滚动模式，记录开始滚动的点
        } else if (action == MotionEvent.ACTION_DOWN) {

            drawn = true;
            //在矩阵动画过程中不允许启动滚动模式
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                //停止所有动画
                cancelAllAnimator();
                //切换到滚动模式
                mPinchMode = PINCH_MODE_SCROLL;
                //保存触发点用于move计算差值
                mLastMovePoint.set(event.getX(), event.getY());
            }

            //vinh
            rect = canvas.getClipBounds();
            final float x = event.getX();
            final float y = event.getY();
            Log.e("MotionEvent", "==========ACTION_DOWN===" + x + "====" + y);
            mLastTouchX = x;
            mLastTouchY = y;
            mActivePointerId = event.getPointerId(0);





            //非第一个点按下，关闭滚动模式，开启缩放模式，记录缩放模式的一些初始数据
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            //停止所有动画
            cancelAllAnimator();
            //切换到缩放模式
            mPinchMode = PINCH_MODE_SCALE;
            //保存缩放的两个手指
            saveScaleContext(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                //在滚动模式下移动
                if (mPinchMode == PINCH_MODE_SCROLL) {
                    //每次移动产生一个差值累积到图片位置上
                    scrollBy(event.getX() - mLastMovePoint.x, event.getY() - mLastMovePoint.y);
                    //记录新的移动点
                    mLastMovePoint.set(event.getX(), event.getY());
                    //在缩放模式下移动
                } else if (mPinchMode == PINCH_MODE_SCALE && event.getPointerCount() > 1) {
                    //两个缩放点间的距离
                    float distance = MathUtils.getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    //保存缩放点中点
                    float[] lineCenter = MathUtils.getCenterPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    mLastMovePoint.set(lineCenter[0], lineCenter[1]);
                    //处理缩放
                    scale(mScaleCenter, mScaleBase, distance, mLastMovePoint);
                }
            }
        }
        //无论如何都处理各种外部手势
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 让图片移动一段距离
     * <p>
     * 不能移动超过可移动范围,超过了就到可移动范围边界为止.
     *
     * @param xDiff 移动距离
     * @param yDiff 移动距离
     * @return 是否改变了位置
     */
    private boolean scrollBy(float xDiff, float yDiff) {
        if (!isReady()) {
            return false;
        }
        //原图方框
        RectF bound = MathUtils.rectFTake();
        getImageBound(bound);
        //控件大小
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        //如果当前图片宽度小于控件宽度，则不能移动
        if (bound.right - bound.left < displayWidth) {
            xDiff = 0;
            //如果图片左边在移动后超出控件左边
        } else if (bound.left + xDiff > 0) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (bound.left < 0) {
                xDiff = -bound.left;
                //否则无法移动
            } else {
                xDiff = 0;
            }
            //如果图片右边在移动后超出控件右边
        } else if (bound.right + xDiff < displayWidth) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (bound.right > displayWidth) {
                xDiff = displayWidth - bound.right;
                //否则无法移动
            } else {
                xDiff = 0;
            }
        }
        //以下同理
        if (bound.bottom - bound.top < displayHeight) {
            yDiff = 0;
        } else if (bound.top + yDiff > 0) {
            if (bound.top < 0) {
                yDiff = -bound.top;
            } else {
                yDiff = 0;
            }
        } else if (bound.bottom + yDiff < displayHeight) {
            if (bound.bottom > displayHeight) {
                yDiff = displayHeight - bound.bottom;
            } else {
                yDiff = 0;
            }
        }
        MathUtils.rectFGiven(bound);
        //应用移动变换
        mOuterMatrix.postTranslate(xDiff, yDiff);
        dispatchOuterMatrixChanged();
        //触发重绘
        invalidate();
        //检查是否有变化
        if (xDiff != 0 || yDiff != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 记录缩放前的一些信息
     * <p>
     * 保存基础缩放值.
     * 保存图片缩放中点.
     *
     * @param x1 缩放第一个手指
     * @param y1 缩放第一个手指
     * @param x2 缩放第二个手指
     * @param y2 缩放第二个手指
     */
    private void saveScaleContext(float x1, float y1, float x2, float y2) {
        //记录基础缩放值,其中图片缩放比例按照x方向来计算
        //理论上图片应该是等比的,x和y方向比例相同
        //但是有可能外部设定了不规范的值.
        //但是后续的scale操作会将xy不等的缩放值纠正,改成和x方向相同
        mScaleBase = MathUtils.getMatrixScale(mOuterMatrix)[0] / MathUtils.getDistance(x1, y1, x2, y2);
        //两手指的中点在屏幕上落在了图片的某个点上,图片上的这个点在经过总矩阵变换后和手指中点相同
        //现在我们需要得到图片上这个点在图片是fit center状态下在屏幕上的位置
        //因为后续的计算都是基于图片是fit center状态下进行变换
        //所以需要把两手指中点除以外层变换矩阵得到mScaleCenter
        float[] center = MathUtils.inverseMatrixPoint(MathUtils.getCenterPoint(x1, y1, x2, y2), mOuterMatrix);
        mScaleCenter.set(center[0], center[1]);
    }

    /**
     * 对图片按照一些手势信息进行缩放
     *
     * @param scaleCenter mScaleCenter
     * @param scaleBase   mScaleBase
     * @param distance    手指两点之间距离
     * @param lineCenter  手指两点之间中点
     * @see #mScaleCenter
     * @see #mScaleBase
     */
    private void scale(PointF scaleCenter, float scaleBase, float distance, PointF lineCenter) {
        if (!isReady()) {
            return;
        }
        //计算图片从fit center状态到目标状态的缩放比例
        float scale = scaleBase * distance;
        Matrix matrix = MathUtils.matrixTake();
        //按照图片缩放中心缩放，并且让缩放中心在缩放点中点上
        matrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y);
        //让图片的缩放中点跟随手指缩放中点
        matrix.postTranslate(lineCenter.x - scaleCenter.x, lineCenter.y - scaleCenter.y);
        //应用变换
        mOuterMatrix.set(matrix);
        MathUtils.matrixGiven(matrix);
        dispatchOuterMatrixChanged();
        //重绘
        invalidate();
    }

    /**
     * 双击后放大或者缩小
     * <p>
     * 将图片缩放比例缩放到nextScale指定的值.
     * 但nextScale值不能大于最大缩放值不能小于fit center情况下的缩放值.
     * 将双击的点尽量移动到控件中心.
     *
     * @param x 双击的点
     * @param y 双击的点
     * @see #calculateNextScale(float, float)
     * @see #getMaxScale()
     */
    private void doubleTap(float x, float y) {
        if (!isReady()) {
            return;
        }
        //获取第一层变换矩阵
        Matrix innerMatrix = MathUtils.matrixTake();
        getInnerMatrix(innerMatrix);
        //当前总的缩放比例
        float innerScale = MathUtils.getMatrixScale(innerMatrix)[0];
        float outerScale = MathUtils.getMatrixScale(mOuterMatrix)[0];
        float currentScale = innerScale * outerScale;
        //控件大小
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        //最大放大大小
        float maxScale = getMaxScale();
        //接下来要放大的大小
        float nextScale = calculateNextScale(innerScale, outerScale);
        //如果接下来放大大于最大值或者小于fit center值，则取边界
        if (nextScale > maxScale) {
            nextScale = maxScale;
        }
        if (nextScale < innerScale) {
            nextScale = innerScale;
        }
        //开始计算缩放动画的结果矩阵
        Matrix animEnd = MathUtils.matrixTake(mOuterMatrix);
        //计算还需缩放的倍数
        animEnd.postScale(nextScale / currentScale, nextScale / currentScale, x, y);
        //将放大点移动到控件中心
        animEnd.postTranslate(displayWidth / 2f - x, displayHeight / 2f - y);
        //得到放大之后的图片方框
        Matrix testMatrix = MathUtils.matrixTake(innerMatrix);
        testMatrix.postConcat(animEnd);
        RectF testBound = MathUtils.rectFTake(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        testMatrix.mapRect(testBound);
        //修正位置
        float postX = 0;
        float postY = 0;
        if (testBound.right - testBound.left < displayWidth) {
            postX = displayWidth / 2f - (testBound.right + testBound.left) / 2f;
        } else if (testBound.left > 0) {
            postX = -testBound.left;
        } else if (testBound.right < displayWidth) {
            postX = displayWidth - testBound.right;
        }
        if (testBound.bottom - testBound.top < displayHeight) {
            postY = displayHeight / 2f - (testBound.bottom + testBound.top) / 2f;
        } else if (testBound.top > 0) {
            postY = -testBound.top;
        } else if (testBound.bottom < displayHeight) {
            postY = displayHeight - testBound.bottom;
        }
        //应用修正位置
        animEnd.postTranslate(postX, postY);
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        //启动矩阵动画
        mScaleAnimator = new ScaleAnimator(mOuterMatrix, animEnd);
        mScaleAnimator.start();
        //清理临时变量
        MathUtils.rectFGiven(testBound);
        MathUtils.matrixGiven(testMatrix);
        MathUtils.matrixGiven(animEnd);
        MathUtils.matrixGiven(innerMatrix);
    }

    /**
     * 当缩放操作结束动画
     * <p>
     * 如果图片超过边界,找到最近的位置动画恢复.
     * 如果图片缩放尺寸超过最大值或者最小值,找到最近的值动画恢复.
     */
    private void scaleEnd() {
        if (!isReady()) {
            return;
        }
        //是否修正了位置
        boolean change = false;
        //获取图片整体的变换矩阵
        Matrix currentMatrix = MathUtils.matrixTake();
        getCurrentImageMatrix(currentMatrix);
        //整体缩放比例
        float currentScale = MathUtils.getMatrixScale(currentMatrix)[0];
        //第二层缩放比例
        float outerScale = MathUtils.getMatrixScale(mOuterMatrix)[0];
        //控件大小
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        //最大缩放比例
        float maxScale = getMaxScale();
        //比例修正
        float scalePost = 1f;
        //位置修正
        float postX = 0;
        float postY = 0;
        //如果整体缩放比例大于最大比例，进行缩放修正
        if (currentScale > maxScale) {
            scalePost = maxScale / currentScale;
        }
        //如果缩放修正后整体导致第二层缩放小于1（就是图片比fit center状态还小），重新修正缩放
        if (outerScale * scalePost < 1f) {
            scalePost = 1f / outerScale;
        }
        //如果缩放修正不为1，说明进行了修正
        if (scalePost != 1f) {
            change = true;
        }
        //尝试根据缩放点进行缩放修正
        Matrix testMatrix = MathUtils.matrixTake(currentMatrix);
        testMatrix.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
        RectF testBound = MathUtils.rectFTake(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        //获取缩放修正后的图片方框
        testMatrix.mapRect(testBound);
        //检测缩放修正后位置有无超出，如果超出进行位置修正
        if (testBound.right - testBound.left < displayWidth) {
            postX = displayWidth / 2f - (testBound.right + testBound.left) / 2f;
        } else if (testBound.left > 0) {
            postX = -testBound.left;
        } else if (testBound.right < displayWidth) {
            postX = displayWidth - testBound.right;
        }
        if (testBound.bottom - testBound.top < displayHeight) {
            postY = displayHeight / 2f - (testBound.bottom + testBound.top) / 2f;
        } else if (testBound.top > 0) {
            postY = -testBound.top;
        } else if (testBound.bottom < displayHeight) {
            postY = displayHeight - testBound.bottom;
        }
        //如果位置修正不为0，说明进行了修正
        if (postX != 0 || postY != 0) {
            change = true;
        }
        //只有有执行修正才执行动画
        if (change) {
            //计算结束矩阵
            Matrix animEnd = MathUtils.matrixTake(mOuterMatrix);
            animEnd.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y);
            animEnd.postTranslate(postX, postY);
            //清理当前可能正在执行的动画
            cancelAllAnimator();
            //启动矩阵动画
            mScaleAnimator = new ScaleAnimator(mOuterMatrix, animEnd);
            mScaleAnimator.start();
            //清理临时变量
            MathUtils.matrixGiven(animEnd);
        }
        //清理临时变量
        MathUtils.rectFGiven(testBound);
        MathUtils.matrixGiven(testMatrix);
        MathUtils.matrixGiven(currentMatrix);
    }

    /**
     * 执行惯性动画
     * <p>
     * 动画在遇到不能移动就停止.
     * 动画速度衰减到很小就停止.
     * <p>
     * 其中参数速度单位为 像素/秒
     *
     * @param vx x方向速度
     * @param vy y方向速度
     */
    private void fling(float vx, float vy) {
        if (!isReady()) {
            return;
        }
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        //创建惯性动画
        //FlingAnimator单位为 像素/帧,一秒60帧
        mFlingAnimator = new FlingAnimator(vx / 60f, vy / 60f);
        mFlingAnimator.start();
    }

    /**
     * 停止所有手势动画
     */
    private void cancelAllAnimator() {
        if (mScaleAnimator != null) {
            mScaleAnimator.cancel();
            mScaleAnimator = null;
        }
        if (mFlingAnimator != null) {
            mFlingAnimator.cancel();
            mFlingAnimator = null;
        }
    }

    /**
     * 惯性动画
     * <p>
     * 速度逐渐衰减,每帧速度衰减为原来的FLING_DAMPING_FACTOR,当速度衰减到小于1时停止.
     * 当图片不能移动时,动画停止.
     */
    private class FlingAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        /**
         * 速度向量
         */
        private float[] mVector;

        /**
         * 创建惯性动画
         * <p>
         * 参数单位为 像素/帧
         *
         * @param vectorX 速度向量
         * @param vectorY 速度向量
         */
        public FlingAnimator(float vectorX, float vectorY) {
            super();
            setFloatValues(0, 1f);
            setDuration(1000000);
            addUpdateListener(this);
            mVector = new float[]{vectorX, vectorY};
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //移动图像并给出结果
            boolean result = scrollBy(mVector[0], mVector[1]);
            //衰减速度
            mVector[0] *= FLING_DAMPING_FACTOR;
            mVector[1] *= FLING_DAMPING_FACTOR;
            //速度太小或者不能移动了就结束
            if (!result || MathUtils.getDistance(0, 0, mVector[0], mVector[1]) < 1f) {
                animation.cancel();
            }
        }
    }

    /**
     * 缩放动画
     * <p>
     * 在给定时间内从一个矩阵的变化逐渐动画到另一个矩阵的变化
     */
    private class ScaleAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        /**
         * 开始矩阵
         */
        private float[] mStart = new float[9];

        /**
         * 结束矩阵
         */
        private float[] mEnd = new float[9];

        /**
         * 中间结果矩阵
         */
        private float[] mResult = new float[9];

        /**
         * 构建一个缩放动画
         * <p>
         * 从一个矩阵变换到另外一个矩阵
         *
         * @param start 开始矩阵
         * @param end   结束矩阵
         */
        public ScaleAnimator(Matrix start, Matrix end) {
            this(start, end, SCALE_ANIMATOR_DURATION);
        }

        /**
         * 构建一个缩放动画
         * <p>
         * 从一个矩阵变换到另外一个矩阵
         *
         * @param start    开始矩阵
         * @param end      结束矩阵
         * @param duration 动画时间
         */
        public ScaleAnimator(Matrix start, Matrix end, long duration) {
            super();
            setFloatValues(0, 1f);
            setDuration(duration);
            addUpdateListener(this);
            start.getValues(mStart);
            end.getValues(mEnd);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //获取动画进度
            float value = (Float) animation.getAnimatedValue();
            //根据动画进度计算矩阵中间插值
            for (int i = 0; i < 9; i++) {
                mResult[i] = mStart[i] + (mEnd[i] - mStart[i]) * value;
            }
            //设置矩阵并重绘
            mOuterMatrix.setValues(mResult);
            dispatchOuterMatrixChanged();
            invalidate();
        }
    }


    ////////////////////////////////防止内存抖动复用对象////////////////////////////////

    /**
     * 对象池
     * <p>
     * 防止频繁new对象产生内存抖动.
     * 由于对象池最大长度限制,如果吞度量超过对象池容量,仍然会发生抖动.
     * 此时需要增大对象池容量,但是会占用更多内存.
     *
     * @param <T> 对象池容纳的对象类型
     */
    private static abstract class ObjectsPool<T> {

        /**
         * 对象池的最大容量
         */
        private int mSize;

        /**
         * 对象池队列
         */
        private Queue<T> mQueue;

        /**
         * 创建一个对象池
         *
         * @param size 对象池最大容量
         */
        public ObjectsPool(int size) {
            mSize = size;
            mQueue = new LinkedList<T>();
        }

        /**
         * 获取一个空闲的对象
         * <p>
         * 如果对象池为空,则对象池自己会new一个返回.
         * 如果对象池内有对象,则取一个已存在的返回.
         * take出来的对象用完要记得调用given归还.
         * 如果不归还,让然会发生内存抖动,但不会引起泄漏.
         *
         * @return 可用的对象
         * @see #given(Object)
         */
        public T take() {
            //如果池内为空就创建一个
            if (mQueue.size() == 0) {
                return newInstance();
            } else {
                //对象池里有就从顶端拿出来一个返回
                return resetInstance(mQueue.poll());
            }
        }

        /**
         * 归还对象池内申请的对象
         * <p>
         * 如果归还的对象数量超过对象池容量,那么归还的对象就会被丢弃.
         *
         * @param obj 归还的对象
         * @see #take()
         */
        public void given(T obj) {
            //如果对象池还有空位子就归还对象
            if (obj != null && mQueue.size() < mSize) {
                mQueue.offer(obj);
            }
        }

        /**
         * 实例化对象
         *
         * @return 创建的对象
         */
        abstract protected T newInstance();

        /**
         * 重置对象
         * <p>
         * 把对象数据清空到就像刚创建的一样.
         *
         * @param obj 需要被重置的对象
         * @return 被重置之后的对象
         */
        abstract protected T resetInstance(T obj);
    }

    /**
     * 矩阵对象池
     */
    private static class MatrixPool extends ObjectsPool<Matrix> {

        public MatrixPool(int size) {
            super(size);
        }

        @Override
        protected Matrix newInstance() {
            return new Matrix();
        }

        @Override
        protected Matrix resetInstance(Matrix obj) {
            obj.reset();
            return obj;
        }
    }

    /**
     * 矩形对象池
     */
    private static class RectFPool extends ObjectsPool<RectF> {

        public RectFPool(int size) {
            super(size);
        }

        @Override
        protected RectF newInstance() {
            return new RectF();
        }

        @Override
        protected RectF resetInstance(RectF obj) {
            obj.setEmpty();
            return obj;
        }
    }


    ////////////////////////////////数学计算工具类////////////////////////////////

    /**
     * 数学计算工具类
     */
    public static class MathUtils {

        /**
         * 矩阵对象池
         */
        private static MatrixPool mMatrixPool = new MatrixPool(16);

        /**
         * 获取矩阵对象
         */
        public static Matrix matrixTake() {
            return mMatrixPool.take();
        }

        /**
         * 获取某个矩阵的copy
         */
        public static Matrix matrixTake(Matrix matrix) {
            Matrix result = mMatrixPool.take();
            if (matrix != null) {
                result.set(matrix);
            }
            return result;
        }

        /**
         * 归还矩阵对象
         */
        public static void matrixGiven(Matrix matrix) {
            mMatrixPool.given(matrix);
        }

        /**
         * 矩形对象池
         */
        private static RectFPool mRectFPool = new RectFPool(16);

        /**
         * 获取矩形对象
         */
        public static RectF rectFTake() {
            return mRectFPool.take();
        }

        /**
         * 按照指定值获取矩形对象
         */
        public static RectF rectFTake(float left, float top, float right, float bottom) {
            RectF result = mRectFPool.take();
            result.set(left, top, right, bottom);
            return result;
        }

        /**
         * 获取某个矩形的副本
         */
        public static RectF rectFTake(RectF rectF) {
            RectF result = mRectFPool.take();
            if (rectF != null) {
                result.set(rectF);
            }
            return result;
        }

        /**
         * 归还矩形对象
         */
        public static void rectFGiven(RectF rectF) {
            mRectFPool.given(rectF);
        }

        /**
         * 获取两点之间距离
         *
         * @param x1 点1
         * @param y1 点1
         * @param x2 点2
         * @param y2 点2
         * @return 距离
         */
        public static float getDistance(float x1, float y1, float x2, float y2) {
            float x = x1 - x2;
            float y = y1 - y2;
            return (float) Math.sqrt(x * x + y * y);
        }

        /**
         * 获取两点的中点
         *
         * @param x1 点1
         * @param y1 点1
         * @param x2 点2
         * @param y2 点2
         * @return float[]{x, y}
         */
        public static float[] getCenterPoint(float x1, float y1, float x2, float y2) {
            return new float[]{(x1 + x2) / 2f, (y1 + y2) / 2f};
        }

        /**
         * 获取矩阵的缩放值
         *
         * @param matrix 要计算的矩阵
         * @return float[]{scaleX, scaleY}
         */
        public static float[] getMatrixScale(Matrix matrix) {
            if (matrix != null) {
                float[] value = new float[9];
                matrix.getValues(value);
                return new float[]{value[0], value[4]};
            } else {
                return new float[2];
            }
        }

        /**
         * 计算点除以矩阵的值
         * <p>
         * matrix.mapPoints(unknownPoint) -> point
         * 已知point和matrix,求unknownPoint的值.
         *
         * @param point
         * @param matrix
         * @return unknownPoint
         */
        public static float[] inverseMatrixPoint(float[] point, Matrix matrix) {
            if (point != null && matrix != null) {
                float[] dst = new float[2];
                //计算matrix的逆矩阵
                Matrix inverse = matrixTake();
                matrix.invert(inverse);
                //用逆矩阵变换point到dst,dst就是结果
                inverse.mapPoints(dst, point);
                //清除临时变量
                matrixGiven(inverse);
                return dst;
            } else {
                return new float[2];
            }
        }

        /**
         * 计算两个矩形之间的变换矩阵
         * <p>
         * unknownMatrix.mapRect(to, from)
         * 已知from矩形和to矩形,求unknownMatrix
         *
         * @param from
         * @param to
         * @param result unknownMatrix
         */
        public static void calculateRectTranslateMatrix(RectF from, RectF to, Matrix result) {
            if (from == null || to == null || result == null) {
                return;
            }
            if (from.width() == 0 || from.height() == 0) {
                return;
            }
            result.reset();
            result.postTranslate(-from.left, -from.top);
            result.postScale(to.width() / from.width(), to.height() / from.height());
            result.postTranslate(to.left, to.top);
        }

        /**
         * 计算图片在某个ImageView中的显示矩形
         *
         * @param container ImageView的Rect
         * @param srcWidth  图片的宽度
         * @param srcHeight 图片的高度
         * @param scaleType 图片在ImageView中的ScaleType
         * @param result    图片应该在ImageView中展示的矩形
         */
        public static void calculateScaledRectInContainer(RectF container, float srcWidth, float srcHeight, ScaleType scaleType, RectF result) {
            if (container == null || result == null) {
                return;
            }
            if (srcWidth == 0 || srcHeight == 0) {
                return;
            }
            //默认scaleType为fit center
            if (scaleType == null) {
                scaleType = ScaleType.FIT_CENTER;
            }
            result.setEmpty();
            if (ScaleType.FIT_XY.equals(scaleType)) {
                result.set(container);
            } else if (ScaleType.CENTER.equals(scaleType)) {
                Matrix matrix = matrixTake();
                RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
                matrix.setTranslate((container.width() - srcWidth) * 0.5f, (container.height() - srcHeight) * 0.5f);
                matrix.mapRect(result, rect);
                rectFGiven(rect);
                matrixGiven(matrix);
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
            } else if (ScaleType.CENTER_CROP.equals(scaleType)) {
                Matrix matrix = matrixTake();
                RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
                float scale;
                float dx = 0;
                float dy = 0;
                if (srcWidth * container.height() > container.width() * srcHeight) {
                    scale = container.height() / srcHeight;
                    dx = (container.width() - srcWidth * scale) * 0.5f;
                } else {
                    scale = container.width() / srcWidth;
                    dy = (container.height() - srcHeight * scale) * 0.5f;
                }
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                matrix.mapRect(result, rect);
                rectFGiven(rect);
                matrixGiven(matrix);
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
            } else if (ScaleType.CENTER_INSIDE.equals(scaleType)) {
                Matrix matrix = matrixTake();
                RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
                float scale;
                float dx;
                float dy;
                if (srcWidth <= container.width() && srcHeight <= container.height()) {
                    scale = 1f;
                } else {
                    scale = Math.min(container.width() / srcWidth, container.height() / srcHeight);
                }
                dx = (container.width() - srcWidth * scale) * 0.5f;
                dy = (container.height() - srcHeight * scale) * 0.5f;
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                matrix.mapRect(result, rect);
                rectFGiven(rect);
                matrixGiven(matrix);
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
            } else if (ScaleType.FIT_CENTER.equals(scaleType)) {
                Matrix matrix = matrixTake();
                RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
                RectF tempSrc = rectFTake(0, 0, srcWidth, srcHeight);
                RectF tempDst = rectFTake(0, 0, container.width(), container.height());
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
                matrix.mapRect(result, rect);
                rectFGiven(tempDst);
                rectFGiven(tempSrc);
                rectFGiven(rect);
                matrixGiven(matrix);
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
            } else if (ScaleType.FIT_START.equals(scaleType)) {
                Matrix matrix = matrixTake();
                RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
                RectF tempSrc = rectFTake(0, 0, srcWidth, srcHeight);
                RectF tempDst = rectFTake(0, 0, container.width(), container.height());
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.START);
                matrix.mapRect(result, rect);
                rectFGiven(tempDst);
                rectFGiven(tempSrc);
                rectFGiven(rect);
                matrixGiven(matrix);
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
            } else if (ScaleType.FIT_END.equals(scaleType)) {
                Matrix matrix = matrixTake();
                RectF rect = rectFTake(0, 0, srcWidth, srcHeight);
                RectF tempSrc = rectFTake(0, 0, srcWidth, srcHeight);
                RectF tempDst = rectFTake(0, 0, container.width(), container.height());
                matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.END);
                matrix.mapRect(result, rect);
                rectFGiven(tempDst);
                rectFGiven(tempSrc);
                rectFGiven(rect);
                matrixGiven(matrix);
                result.left += container.left;
                result.right += container.left;
                result.top += container.top;
                result.bottom += container.top;
            } else {
                result.set(container);
            }
        }
    }
}