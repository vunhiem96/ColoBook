/**
 * ****************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package com.volio.coloringbook2.customview.photoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.*;
import android.view.View.OnLongClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.volio.coloringbook2.customview.ColourImageView;
import com.volio.coloringbook2.customview.photoview.gestures.OnGestureListener;
import com.volio.coloringbook2.customview.photoview.gestures.VersionedGestureDetector;
import com.volio.coloringbook2.customview.photoview.log.LogManager;
import com.volio.coloringbook2.customview.photoview.scrollerproxy.ScrollerProxy;

import java.lang.ref.WeakReference;

import static android.view.MotionEvent.*;

public class PhotoViewAttacher implements IPhotoView, View.OnTouchListener,
        OnGestureListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private static final String LOG_TAG = "PhotoViewAttacher";

    // let debug flag be dynamic, but still Proguard can be used to remove from
    // release builds
    private static final boolean DEBUG = Log.isLoggable(LOG_TAG, Log.DEBUG);

    static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
    int ZOOM_DURATION = DEFAULT_ZOOM_DURATION;

    static final int EDGE_NONE = -1;
    static final int EDGE_LEFT = 0;
    static final int EDGE_RIGHT = 1;
    static final int EDGE_BOTH = 2;

    private float mMinScale = 0.5f; //change by swifty
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = 8.0f; //change by swifty

    private boolean mAllowParentInterceptOnEdge = true;
    private boolean mBlockParentIntercept = false;
    public static boolean running = false;

    private static void checkZoomLevels(float minZoom, float midZoom,
                                        float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "MinZoom has to be less than MidZoom");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "MidZoom has to be less than MaxZoom");
        }
    }

    /**
     * @return true if the ImageView exists, and it's Drawable existss
     */
    private static boolean hasDrawable(ImageView imageView) {
        return null != imageView && null != imageView.getDrawable();
    }

    /**
     * @return true if the ScaleType is supported.
     */
    private static boolean isSupportedScaleType(final ScaleType scaleType) {
        if (null == scaleType) {
            return false;
        }

        switch (scaleType) {
            case MATRIX:
                throw new IllegalArgumentException(scaleType.name()
                        + " is not supported in PhotoView");

            default:
                return true;
        }
    }

    /**
     * Set's the ImageView's ScaleType to Matrix.
     */
    private static void setImageViewScaleTypeMatrix(ImageView imageView) {
        /**
         * PhotoView sets it's own ScaleType to Matrix, then diverts all calls
         * setScaleType to this.setScaleType automatically.
         */
        if (null != imageView && !(imageView instanceof IPhotoView)) {
            if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
                imageView.setScaleType(ScaleType.MATRIX);

            }
        }


    }

    private WeakReference<ImageView> mImageView;

    // Gesture Detectors
    private GestureDetector mGestureDetector;
    private com.volio.coloringbook2.customview.photoview.gestures.GestureDetector mScaleDragDetector;

    // These are set so we don't keep allocating them on the heap
    private Matrix mBaseMatrix = new Matrix();
    private Matrix mDrawMatrix = new Matrix();
    private Matrix mSuppMatrix = new Matrix();
    private RectF mDisplayRect = new RectF();
    private float[] mMatrixValues = new float[9];

    // Listeners
    private OnMatrixChangedListener mMatrixChangeListener;
    private OnPhotoTapListener mPhotoTapListener;
    private OnViewTapListener mViewTapListener;
    private OnLongClickListener mLongClickListener;
    private OnScaleChangeListener mScaleChangeListener;

    private int mIvTop, mIvRight, mIvBottom, mIvLeft;
    private FlingRunnable mCurrentFlingRunnable;
    private int mScrollEdge = EDGE_BOTH;

    private boolean mZoomEnabled;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;
    boolean move = false;
//    public PhotoViewAttacher(ImageView imageView) {
//        this(imageView, true);
//    }

    private PhotoViewAttacher(ImageView imageView, boolean zoomable, Bitmap bm) {
        this.ZOOM_DURATION = 200;
        this.mMinScale = 0.5f;
        this.mMidScale = 1.75f;
        this.mMaxScale = 8.0f;
        this.mAllowParentInterceptOnEdge = true;
        this.mBlockParentIntercept = false;
        this.mBaseMatrix = new Matrix();
        this.mDrawMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mDisplayRect = new RectF();
        this.mMatrixValues = new float[9];
        this.mScrollEdge = 2;
        this.mScaleType = ScaleType.FIT_CENTER;

        this.move = false;
        this.mImageView = new WeakReference<>(imageView);
        if (imageView == null) return;
        imageView.setDrawingCacheEnabled(true);
        imageView.setOnTouchListener(this);

//        ((ColourImageView) imageView).createBitMap(bm);

        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (observer != null) {
            observer.addOnGlobalLayoutListener(this);
        }
        setImageViewScaleTypeMatrix(imageView);
        if (!imageView.isInEditMode()) {
            this.mScaleDragDetector = VersionedGestureDetector.newInstance(imageView.getContext(), this);
            this.mGestureDetector = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                public void onLongPress(MotionEvent e) {
                    if (PhotoViewAttacher.this.mLongClickListener != null) {
                        PhotoViewAttacher.this.mLongClickListener.onLongClick(PhotoViewAttacher.this.getImageView());
                    }
                }
            });
            setZoomable(zoomable);
        }

    }


    private static final String TAG = "dsk";

    public PhotoViewAttacher(ColourImageView imageView, Bitmap bt) {
        this(imageView, true, bt);
        Log.d(TAG, "PhotoViewAttacher: ");
//        if (imageView != null) {
//            ((TestImage) imageView).createBitMap(bt);
//        }
    }


    @Override
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener) {
        if (newOnDoubleTapListener != null) {
            this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
        } else {
            this.mGestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
        }
    }

    @Override
    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.mScaleChangeListener = onScaleChangeListener;
    }

    @Override
    public boolean canZoom() {
        return mZoomEnabled;
    }


    @SuppressWarnings("deprecation")
    public void cleanup() {
        if (null == mImageView) {
            return; // cleanup already done
        }

        final ImageView imageView = mImageView.get();

        if (null != imageView) {
            // Remove this as a global layout listener
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (null != observer && observer.isAlive()) {
                observer.removeGlobalOnLayoutListener(this);
            }

            // Remove the ImageView's reference to this
            imageView.setOnTouchListener(null);

            // make sure a pending fling runnable won't be run
            cancelFling();
        }

        if (null != mGestureDetector) {
            mGestureDetector.setOnDoubleTapListener(null);
        }

        // Clear listeners too
        mMatrixChangeListener = null;
        mPhotoTapListener = null;
        mViewTapListener = null;

        // Finally, clear ImageView
        mImageView = null;
    }

    @Override
    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    @Override
    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null)
            throw new IllegalArgumentException("Matrix cannot be null");

        ImageView imageView = getImageView();
        if (null == imageView)
            return false;

        if (null == imageView.getDrawable())
            return false;

        mSuppMatrix.set(finalMatrix);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();

        return true;
    }

    /**
     * @deprecated use {@link #setRotationTo(float)}
     */
    @Override
    public void setPhotoViewRotation(float degrees) {
        mSuppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    @Override
    public void setRotationTo(float degrees) {
        mSuppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    @Override
    public void setRotationBy(float degrees) {
        mSuppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public ImageView getImageView() {
        ImageView imageView = null;

        if (null != mImageView) {
            imageView = mImageView.get();
//            Log.d(TAG, "imageView: " + imageView.toString());
        }

        // If we don't have an ImageView, call cleanup()
        if (null == imageView) {
            cleanup();

        }

        return imageView;
    }

    @Override
    @Deprecated
    public float getMinScale() {
        return getMinimumScale();
    }

    @Override
    public float getMinimumScale() {
        return mMinScale;
    }

    @Override
    @Deprecated
    public float getMidScale() {
        return getMediumScale();
    }

    @Override
    public float getMediumScale() {
        return mMidScale;
    }

    @Override
    @Deprecated
    public float getMaxScale() {
        return getMaximumScale();
    }

    @Override
    public float getMaximumScale() {
        return mMaxScale;
    }

    @Override
    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    @Override
    public ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public void onDrag(float dx, float dy) {
        Log.d(TAG, "onDrag: ");

        move = true;
        if (mScaleDragDetector.isScaling()) {
            return; // Do not drag if we are already scaling
        }


        ImageView imageView = getImageView();
        mSuppMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();

        /**
         * Here we decide whether to let the ImageView's parent to start taking
         * over the touch event.
         *
         * First we check whether this function is enabled. We never want the
         * parent to take over if we're scaling. We then check the edge we're
         * on, and the direction of the scroll (i.e. if we're pulling against
         * the edge, aka 'overscrolling', let the parent take over).
         */
        ViewParent parent = imageView.getParent();
        if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept) {
            if (mScrollEdge == EDGE_BOTH
                    || (mScrollEdge == EDGE_LEFT && dx >= 1f)
                    || (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
                if (null != parent)
                    parent.requestDisallowInterceptTouchEvent(false);
            }
        } else {
            if (null != parent) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX,
                        float velocityY) {

        ImageView imageView = getImageView();
        mCurrentFlingRunnable = new FlingRunnable(imageView.getContext());
        mCurrentFlingRunnable.fling(getImageViewWidth(imageView),
                getImageViewHeight(imageView), (int) velocityX, (int) velocityY);
        imageView.post(mCurrentFlingRunnable);
    }

    @Override
    public void onGlobalLayout() {
        ImageView imageView = getImageView();
        if (null != imageView) {
            if (mZoomEnabled) {
                int top = imageView.getTop();
                int right = imageView.getRight();
                int bottom = imageView.getBottom();
                int left = imageView.getLeft();

                if (top != mIvTop || bottom != mIvBottom || left != mIvLeft
                        || right != mIvRight) {
                    // Update our base matrix, as the bounds have changed
                    updateBaseMatrix(imageView.getDrawable());

                    // Update values as something has changed
                    this.mIvTop = top;
                    this.mIvRight = right;
                    this.mIvBottom = bottom;
                    this.mIvLeft = left;
                }
            } else {
                updateBaseMatrix(imageView.getDrawable());
            }
            getDisplayMatrix();
            checkAndDisplayMatrix();
        }
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {


        if (getScale() < mMaxScale || scaleFactor < 1f) {
            if (null != mScaleChangeListener) {
                mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
            }
            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        boolean handled = false;
        if (mZoomEnabled && hasDrawable((ImageView) v)) {
            ViewParent parent = v.getParent();
            switch (ev.getAction()) {
                case ACTION_DOWN:
                    // First, disable the Parent from intercepting the touch
                    // event
                    if (null != parent) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    } else {
                        LogManager.getLogger().i(LOG_TAG, "onTouch getParent() returned null");
                    }
                    // If we're flinging, and the user presses down, cancel
                    // fling
                    cancelFling();
//                    TouchEffectFactory.getInstance().showTouchEffect((ViewGroup) getImageView().getParent(), getImageView().getContext(), ev.getX(), ev.getY());
                    break;

                case ACTION_CANCEL:
                case ACTION_UP:
                    // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (getScale() < mMinScale) {
                        RectF rect = getDisplayRect();
                        if (null != rect) {
                            v.post(new AnimatedZoomRunnable(getScale(), mMinScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    }
                    if (!move && !running) {
                        running = true;
                        fillColor((ImageView) v, ev);
                        running = false;
                    } else {
                        move = false;
                    }
                    break;
            }

            // Try the Scale/Drag detector
            if (null != mScaleDragDetector) {

                boolean wasScaling = mScaleDragDetector.isScaling();
                boolean wasDragging = mScaleDragDetector.isDragging();

                handled = mScaleDragDetector.onTouchEvent(ev);

                boolean didntScale = !wasScaling && !mScaleDragDetector.isScaling();
                boolean didntDrag = !wasDragging && !mScaleDragDetector.isDragging();

                mBlockParentIntercept = didntScale && didntDrag;
            }

            // Check to see if the user double tapped
            if (null != mGestureDetector && mGestureDetector.onTouchEvent(ev)) {
                handled = true;
            }
        }

        return handled;
    }

    private void fillColor(ImageView imageView, MotionEvent ev) {
        if (!move) {
            Matrix inverse = new Matrix();
            imageView.getImageMatrix().invert(inverse);
            float[] touchPoint = new float[]{ev.getX(), ev.getY()};
            inverse.mapPoints(touchPoint);
            int x = (int) touchPoint[0];
            int y = (int) touchPoint[1];
//            if (((ColourImageView) imageView).getModel() == ColourImageView.Model.PICKCOLOR) {
//                ((ColourImageView) imageView).pickColor(x, y);
//            } else if (((ColourImageView) imageView).getModel() == ColourImageView.Model.DRAW_LINE) {
//                ((ColourImageView) imageView).drawLine(x, y);
//            } else {
            ((ColourImageView) imageView).fillColorToSameArea(x, y);
//            }
//            ((TestImage) imageView).fillColor(x,y);

        }
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAllowParentInterceptOnEdge = allow;
    }

    @Override
    @Deprecated
    public void setMinScale(float minScale) {
        setMinimumScale(minScale);
    }

    @Override
    public void setMinimumScale(float minimumScale) {
        checkZoomLevels(minimumScale, mMidScale, mMaxScale);
        mMinScale = minimumScale;
    }

    @Override
    @Deprecated
    public void setMidScale(float midScale) {
        setMediumScale(midScale);
    }

    @Override
    public void setMediumScale(float mediumScale) {
        checkZoomLevels(mMinScale, mediumScale, mMaxScale);
        mMidScale = mediumScale;
    }

    @Override
    @Deprecated
    public void setMaxScale(float maxScale) {
        setMaximumScale(maxScale);
    }

    @Override
    public void setMaximumScale(float maximumScale) {
        checkZoomLevels(mMinScale, mMidScale, maximumScale);
        mMaxScale = maximumScale;
    }

    @Override
    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        checkZoomLevels(minimumScale, mediumScale, maximumScale);
        mMinScale = minimumScale;
        mMidScale = mediumScale;
        mMaxScale = maximumScale;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    @Override
    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mMatrixChangeListener = listener;
    }

    @Override
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mPhotoTapListener = listener;
    }

    @Override
    public OnPhotoTapListener getOnPhotoTapListener() {
        return mPhotoTapListener;
    }

    @Override
    public void setOnViewTapListener(OnViewTapListener listener) {
        mViewTapListener = listener;
    }

    @Override
    public OnViewTapListener getOnViewTapListener() {
        return mViewTapListener;
    }

    @Override
    public void setScale(float scale) {
        setScale(scale, false);
    }

    @Override
    public void setScale(float scale, boolean animate) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            setScale(scale,
                    (imageView.getRight()) / 2,
                    (imageView.getBottom()) / 2,
                    animate);
        }
    }

    @Override
    public void setScale(float scale, float focalX, float focalY,
                         boolean animate) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            // Check to see if the scale is within bounds
            if (scale < mMinScale || scale > mMaxScale) {

                return;
            }

            if (animate) {
                imageView.post(new AnimatedZoomRunnable(getScale(), scale,
                        focalX, focalY));
            } else {
                mSuppMatrix.setScale(scale, scale, focalX, focalY);
                checkAndDisplayMatrix();
            }
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {

        if (isSupportedScaleType(scaleType) && scaleType != mScaleType) {
            mScaleType = scaleType;

            // Finally update
            update();
        }
    }

    @Override
    public void setZoomable(boolean zoomable) {
        mZoomEnabled = zoomable;
        update();
    }

    public void update() {
        ImageView imageView = getImageView();
        ((ColourImageView) imageView).update();


        if (null != imageView) {
            if (mZoomEnabled) {
                // Make sure we using MATRIX Scale Type
                setImageViewScaleTypeMatrix(imageView);

                // Update the base matrix using the current drawable
                updateBaseMatrix(imageView.getDrawable());
            } else {
                // Reset the Matrix...
                resetMatrix();
            }
        }
    }

    @Override
    public Matrix getDisplayMatrix() {
        return new Matrix(getDrawMatrix());
    }

    public Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    private void cancelFling() {
        if (null != mCurrentFlingRunnable) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    private void checkImageViewScaleType() {
        ImageView imageView = getImageView();

        /**
         * PhotoView's getScaleType() will just divert to this.getScaleType() so
         * only call if we're not attached to a PhotoView.
         */
        if (null != imageView && !(imageView instanceof IPhotoView)) {
            if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
                throw new IllegalStateException(
                        "The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher");
            }
        }
    }

    private boolean checkMatrixBounds() {
        final ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }

        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight(imageView);
        if (height <= viewHeight) {

            switch (mScaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }

        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getImageViewWidth(imageView);
        if (width <= viewWidth) {

            switch (mScaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }

            mScrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            mScrollEdge = EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mScrollEdge = EDGE_RIGHT;
        } else {
            mScrollEdge = EDGE_NONE;
        }

        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            Drawable d = imageView.getDrawable();
            if (null != d) {
                mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                        d.getIntrinsicHeight());
                matrix.mapRect(mDisplayRect);
                return mDisplayRect;
            }
        }
        return null;
    }

    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = getImageView();
        return imageView == null ? null : imageView.getDrawingCache();
    }

    @Override
    public void setZoomTransitionDuration(int milliseconds) {
        if (milliseconds < 0)
            milliseconds = DEFAULT_ZOOM_DURATION;
        this.ZOOM_DURATION = milliseconds;
    }

    @Override
    public IPhotoView getIPhotoViewImplementation() {
        return this;
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     - Matrix to unpack
     * @param whichValue - Which value from Matrix.M* to return
     * @return float - returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private void resetMatrix() {
        mSuppMatrix.reset();
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        ImageView imageView = getImageView();
        if (null != imageView) {

            checkImageViewScaleType();
            imageView.setImageMatrix(matrix);
            // Call MatrixChangedListener if needed
            if (null != mMatrixChangeListener) {
                RectF displayRect = getDisplayRect(matrix);
                if (null != displayRect) {
                    mMatrixChangeListener.onMatrixChanged(displayRect);
                }
            }
        }
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = getImageView();
        if (imageView != null && d != null) {
            float viewWidth = (float) getImageViewWidth(imageView);
            float viewHeight = (float) getImageViewHeight(imageView);
            int drawableWidth = d.getIntrinsicWidth();
            int drawableHeight = d.getIntrinsicHeight();
            this.mBaseMatrix.reset();
            float widthScale = viewWidth / ((float) drawableWidth);
            float heightScale = viewHeight / ((float) drawableHeight);

            if (this.mScaleType != ScaleType.CENTER) {
                if (this.mScaleType != ScaleType.CENTER_CROP) {
                    if (this.mScaleType != ScaleType.CENTER_INSIDE) {
                        RectF mTempSrc = new RectF(0.0f, 0.0f, (float) drawableWidth, (float) drawableHeight);
                        RectF mTempDst = new RectF(0.0f, 0.0f, viewWidth, viewHeight);
                        switch (this.mScaleType.ordinal()) {
                            case 2:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
                                break;
                            case 3:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
                                break;
                            case 4:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                                break;
                            case 5:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                                break;
                        }
                    } else {
                        float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
                        this.mBaseMatrix.postScale(scale, scale);
                        this.mBaseMatrix.postTranslate((viewWidth - (((float) drawableWidth) * scale)) / 2.0f, (viewHeight - (((float) drawableHeight) * scale)) / 2.0f);
                    }
                } else {
                    float scale2 = Math.max(widthScale, heightScale);
                    this.mBaseMatrix.postScale(scale2, scale2);
                    this.mBaseMatrix.postTranslate((viewWidth - (((float) drawableWidth) * scale2)) / 2.0f, (viewHeight - (((float) drawableHeight) * scale2)) / 2.0f);
                }
            } else {
                this.mBaseMatrix.postTranslate((viewWidth - ((float) drawableWidth)) / 2.0f, (viewHeight - ((float) drawableHeight)) / 2.0f);
            }
            resetMatrix();
        }


//        ImageView imageView = getImageView();
//        if (null == imageView || null == d) {
//            return;
//        }
//
//        final float viewWidth = getImageViewWidth(imageView);
//        final float viewHeight = getImageViewHeight(imageView);
//        final int drawableWidth = d.getIntrinsicWidth();
//        final int drawableHeight = d.getIntrinsicHeight();
//        mBaseMatrix.reset();
//
//        final float widthScale = viewWidth / drawableWidth;
//        final float heightScale = viewHeight / drawableHeight;
//
//        if (mScaleType == ScaleType.CENTER) {
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
//                    (viewHeight - drawableHeight) / 2F);
//
//        } else if (mScaleType == ScaleType.CENTER_CROP) {
//            float scale = Math.max(widthScale, heightScale);
//            mBaseMatrix.postScale(scale, scale);
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
//                    (viewHeight - drawableHeight * scale) / 2F);
//
//        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
//            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
//            mBaseMatrix.postScale(scale, scale);
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
//                    (viewHeight - drawableHeight * scale) / 2F);
//
//        } else {
//            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
//            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);
//
//            switch (mScaleType) {
//                case FIT_CENTER:
//                    mBaseMatrix
//                            .setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
//                    break;
//
//                case FIT_START:
//                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
//                    break;
//
//                case FIT_END:
//                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
//                    break;
//
//                case FIT_XY:
//                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//
//        resetMatrix();
    }

    private int getImageViewWidth(ImageView imageView) {
        if (null == imageView)
            return 0;
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        if (null == imageView)
            return 0;
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }


    /**
     * Interface definition for a callback to be invoked when the internal Matrix has changed for
     * this View.
     *
     * @author Chris Banes
     */
    public static interface OnMatrixChangedListener {
        /**
         * Callback for when the Matrix displaying the Drawable has changed. This could be because
         * the View's bounds have changed, or the user has zoomed.
         *
         * @param rect - Rectangle displaying the Drawable's new bounds.
         */
        void onMatrixChanged(RectF rect);
    }

    /**
     * Interface definition for callback to be invoked when attached ImageView scale changes
     *
     * @author Marek Sebera
     */
    public static interface OnScaleChangeListener {
        /**
         * Callback for when the scale changes
         *
         * @param scaleFactor the scale factor (<1 for zoom out, >1 for zoom in)
         * @param focusX      focal point X position
         * @param focusY      focal point Y position
         */
        void onScaleChange(float scaleFactor, float focusX, float focusY);
    }

    /**
     * Interface definition for a callback to be invoked when the Photo is tapped with a single
     * tap.
     *
     * @author Chris Banes
     */
    public static interface OnPhotoTapListener {

        /**
         * A callback to receive where the user taps on a photo. You will only receive a callback if
         * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the of the Drawable, as percentage of the
         *             Drawable width.
         * @param y    - where the user tapped from the top of the Drawable, as percentage of the
         *             Drawable height.
         */
        void onPhotoTap(View view, float x, float y);
    }

    /**
     * Interface definition for a callback to be invoked when the ImageView is tapped with a single
     * tap.
     *
     * @author Chris Banes
     */
    public static interface OnViewTapListener {

        /**
         * A callback to receive where the user taps on a ImageView. You will receive a callback if
         * the user taps anywhere on the view, tapping on 'whitespace' will not be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the left of the View.
         * @param y    - where the user tapped from the top of the View.
         */
        void onViewTap(View view, float x, float y);
    }

    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {
            ImageView imageView = getImageView();
            if (imageView == null) {
                return;
            }

            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            onScale(deltaScale, mFocalX, mFocalY);

            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(imageView, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION;
            t = Math.min(1f, t);
            t = sInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {

        private final ScrollerProxy mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = ScrollerProxy.getScroller(context);
        }

        public void cancelFling() {

            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final RectF rect = getDisplayRect();
            if (null == rect) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;


            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return; // remaining post that should not be handled
            }

            ImageView imageView = getImageView();
            if (null != imageView && mScroller.computeScrollOffset()) {

                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();


                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                setImageViewMatrix(getDrawMatrix());

                mCurrentX = newX;
                mCurrentY = newY;

                // Post On animation
                Compat.postOnAnimation(imageView, this);
            }
        }
    }
}
