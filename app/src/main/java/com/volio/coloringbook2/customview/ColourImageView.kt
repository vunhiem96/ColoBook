package com.volio.coloringbook2.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import com.volio.coloringbook2.java.DrawModel
import com.volio.coloringbook2.java.PhotorThread
import java.util.*

class ColourImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {


    private var mBitmap: Bitmap? = null

    private val mStacks = Stack<Point>()
    private var mColor = Color.GREEN

    var onRedoUndoListener: OnRedoUndoListener? = null
    private val list = ArrayList<DrawModel>()
    private var current_step = 0
    private var total_step = 0

    var undoSize = 0
    var reduSize = 0

    fun createBitMap(bt: Bitmap) {
        try {
//            mBitmap = bt.copy(bt.config, true)
            mBitmap = bt
            setImageBitmap(bt)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }

    }

    fun fillColorToSameArea(i2: Int, i3: Int) {
        PhotorThread.getInstance().removeAllBackgroundThreads()
        try {
            if (!isBorderColor(this.mBitmap!!.getPixel(i2, i3))) {
                actionFillColor(i2, i3, mColor, true)
                undoSize = 1
                onRedoUndoListener?.onRedoUndo(undoSize, reduSize)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun actionFillColor(i2: Int, i3: Int, mColor: Int, isTouch: Boolean) {
        if (mColor != mBitmap?.getPixel(i2, i3)) {

//            PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
//                override fun doingBackground() {

//            doAsync {
////                actionFillColor2(i2, i3, mColor, isTouch)
////                setImageDrawable(BitmapDrawable(resources, mBitmap))
//            }

            PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
                override fun doingBackground() {
                    actionFillColor2(i2, i3, mColor, isTouch)
                }

                override fun onCompleted() {
                    setImageDrawable(BitmapDrawable(resources, mBitmap))
                }

                override fun onCancel() {
                }

            })


//            val pixel = mBitmap?.getPixel(i2, i3)
//            val width = mBitmap?.width
//            val height = mBitmap?.height
//            if (width == null || height == null) return;
//            val iArr = IntArray(width * height)
//            if (isTouch) {
//                val point = Point(i2, i3)
//                val model = DrawModel(point, pixel!!, mColor)
//                if (current_step < total_step) {
//                    var i = total_step - 1
//                    while (i >= current_step) {
//                        try {
//                            list.removeAt(i)
//                        } catch (e: IndexOutOfBoundsException) {
//                        }
//                        i--
//                    }
//                    current_step++
//                    total_step = current_step
//                } else {
//                    total_step++
//                    current_step = total_step
//                }
//                list.add(model)
//            }
//
//            mBitmap?.getPixels(iArr, 0, width, 0, 0, width, height)
//            try {
//                a(iArr, width, height, pixel!!, mColor, i2, i3)
//                mBitmap?.setPixels(iArr, 0, width, 0, 0, width, height)
//            } catch (e: OutOfMemoryError) {
//                e.printStackTrace()
//            }


//                }
//
//                override fun onCompleted() {
//                    setImageDrawable(BitmapDrawable(resources, mBitmap))
//                }
//
//                override fun onCancel() {}
//            })
        }
    }


    private fun actionFillColor2(i2: Int, i3: Int, mColor: Int, isTouch: Boolean) {
        if (mColor != mBitmap?.getPixel(i2, i3)) {

//            PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
//                override fun doingBackground() {


            val pixel = mBitmap?.getPixel(i2, i3)
            val width = mBitmap?.width
            val height = mBitmap?.height
            if (width == null || height == null) return;
            val iArr = IntArray(width * height)
            if (isTouch) {
                val point = Point(i2, i3)
                val model = DrawModel(point, pixel!!, mColor)
                if (current_step < total_step) {
                    var i = total_step - 1
                    while (i >= current_step) {
                        try {
                            list.removeAt(i)
                        } catch (e: IndexOutOfBoundsException) {
                        }
                        i--
                    }
                    current_step++
                    total_step = current_step
                } else {
                    total_step++
                    current_step = total_step
                }
                list.add(model)
            }

            mBitmap?.getPixels(iArr, 0, width, 0, 0, width, height)
            try {
                a(iArr, width, height, pixel!!, mColor, i2, i3)
                mBitmap?.setPixels(iArr, 0, width, 0, 0, width, height)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }

//                }
//
//                override fun onCompleted() {
//                    setImageDrawable(BitmapDrawable(resources, mBitmap))
//                }
//
//                override fun onCancel() {}
//            })
        }
    }


    fun a(iArr: IntArray, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int) {
        this.mStacks.clear()
        this.mStacks.push(Point(i6, i7))


        while (!this.mStacks.isEmpty()) {
            try {
                val point = this.mStacks.pop()
                val a2 = point.x - a(iArr, i4, i2, i3, i5, point.x, point.y, i6, i7) + 1
                val b2 = point.x + b(iArr, i4, i2, i3, i5, point.x + 1, point.y, i6, i7)
                if (point.y - 1 >= 0) {
                    b(iArr, i4, i2, i3, point.y - 1, a2, b2)
                }
                if (point.y + 1 < i3) {
                    b(iArr, i4, i2, i3, point.y + 1, a2, b2)
                }
            } catch (e: EmptyStackException) {
                e.printStackTrace()
            }

        }
    }

    private fun b(iArr: IntArray, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int) {
        val i8 = i5 * i3
        val i9 = i6 + i8
        var z = false
        for (i10 in i8 + i7 downTo i9) {
            if (!a(iArr, i2, i10)) {
                z = false
            } else if (!z) {
                try {
                    this.mStacks.push(Point(i10 % i3, i5))
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                }

                z = true
            }
        }
    }

    fun update() {
        if (drawable != null) {
            setMeasuredDimension(measuredWidth,
                drawable.intrinsicHeight * measuredWidth / drawable.intrinsicWidth)
        }
    }

    fun getBitmap(): Bitmap? {
        return mBitmap
    }

    fun setColor(color: Int) {
        mColor = color
    }


    fun actionBackStep() {
        if (current_step > 0) {
            if (current_step - 1 < list.size) {
                PhotorThread.getInstance().removeAllBackgroundThreads()
                val model = list[current_step - 1]
                current_step--
                actionFillColor(model.point.x, model.point.y, model.sourceColor, false)
                if (current_step == 0) {
                    undoSize = 0
                }
                reduSize = 1
                onRedoUndoListener?.onRedoUndo(undoSize, reduSize)
            }
        }
    }

    fun actionForwardStep() {
        if (current_step < total_step) {
            PhotorThread.getInstance().removeAllBackgroundThreads()
            val model = list[current_step]
            actionFillColor(model.point.x, model.point.y, model.targetColor, false)
            current_step++
            if (current_step == total_step) {
                reduSize = 0
            }
            undoSize = 1
            onRedoUndoListener?.onRedoUndo(undoSize, reduSize)
        }
    }

    public interface OnRedoUndoListener {
        fun onRedoUndo(undoSize: Int, redoSize: Int)
    }


    private fun b(iArr: IntArray, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): Int {
        var i12 = 0
        for (i13 in i6 until i3) {
            val i14 = i7 * i3 + i13
            if (!a(iArr, i2, i14)) {
                break
            }
            iArr[i14] = i5
            i12++
        }
        return i12
    }

    private fun a(iArr: IntArray, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): Int {
        var i11 = 0
        for (i12 in i6 downTo 0) {
            val i13 = i7 * i3 + i12
            if (!a(iArr, i2, i13)) {
                break
            }
            iArr[i13] = i5
            i11++
        }
        return i11
    }

    private fun a(iArr: IntArray, i2: Int, i3: Int): Boolean {
        var z = false
        if (iArr[i3] == i2) {
            z = true
        }
        return z
    }

    private fun isBorderColor(color: Int): Boolean {
        return Color.red(color) == Color.green(color) && Color.red(color) == Color.blue(color) &&
                Color.green(color) == Color.blue(color) && Color.red(color) == 0
    }


}