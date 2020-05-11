package com.volio.coloringbook2.customview

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.widget.ImageView
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.interfaces.ImageInterface
import com.volio.coloringbook2.java.Lo
import com.volio.coloringbook2.java.PhotorThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MyView2 constructor(context: Context, bm: Bitmap?) : ImageView(context) {

    private var canvas: Canvas? = null
    private var mBitmap: Bitmap? = null
    private var myBitmap: Bitmap? = null
    private val pts = ArrayList<Point>()
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rect: Rect? = null
    private var widthh = 0
    private var heightt = 0
    private var list = mutableListOf<com.volio.coloringbook2.java.DrawModel>()
    private var current_step = 0
    private var total_step = 0
    private var pixelWhite = 0
    private var isEditImage = false
    private var isEraseMode = false
    private var isBackAble = false
    private var isForwardAble = false

    private var imageInterface: ImageInterface? = null

    private val lastTapTime: Long = 0
    private var timeTouchDown: Long = 0


    private var parentHeight = 0
    private var parentWidth = 0

    init {
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 2f
        paint.isDither = true
        paint.color = Color.parseColor("#a6f914")
        paint.isAntiAlias = false
        if (bm != null) {
            myBitmap = bm
        }
    }

    fun setListener(imageInterface: ImageInterface) {
        this.imageInterface = imageInterface
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        this.setMeasuredDimension(parentWidth, parentHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        scaleBitmap(parentWidth, parentHeight)
    }


    fun caculatePixel(): Int {
        val countX = mBitmap?.width
        val countY = mBitmap?.height
        var whiteCount = 0
        if (countX == null || countY == null) return 0
        if (countX > 0 && countY > 0) {
            var rangeX = 0
            while (rangeX < countX) {
                var rangeY = 0
                while (rangeY < countY) {
                    if (rangeX <= countX && rangeY <= countY) {
                        try {
                            val colorXY = mBitmap!!.getPixel(rangeX, rangeY)
                            val r = Color.red(colorXY)
                            val g = Color.green(colorXY)
                            val b = Color.blue(colorXY)
                            if (Color.rgb(r, g, b) == Color.WHITE) {
                                whiteCount++
                            }
                            rangeY += 20
                        } catch (e: IllegalArgumentException) {
                        }
                    }
                }
                rangeX += 20
            }
        }
        return whiteCount
    }

    fun setPixelWhite(white: Int) {
        pixelWhite = white
    }

    private fun updatePerCent(imagename: String) {
        if (pixelWhite > 0) {
            val currentWhite = caculatePixel()
            val dao = CalendarDatabase.invoke(context).calendarDao()
            if (currentWhite < pixelWhite) {
                val inteval = pixelWhite / 100
                if (inteval <= 0) return
                val pecent1 = currentWhite / inteval
                if (pecent1 <= 100) {
                    val percent = 100 - pecent1
                    com.volio.coloringbook2.java.Lo.d("vinh32", "percent $percent")
//                    dao.updatePercentImage(imagename, percent)
                }
            } else {
//                dao.updatePercentImage(imagename, 0)
            }
        }
    }


    fun saveBitmap(name: String, imageName: String) {
        if (isEditImage) {
            val t1 = Thread(Runnable {
                updatePerCent(imageName)
                val bytes = ByteArrayOutputStream()
                mBitmap?.compress(Bitmap.CompressFormat.PNG, 30, bytes)
                val fo: FileOutputStream
                try {
                    fo = FileOutputStream(name)
                    fo.write(bytes.toByteArray())
                    fo.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })
            t1.start()
        }
    }


    private fun scaleBitmap(screenWidth: Int, screenHeight: Int) {
        Lo.d("scaleBitmap $screenWidth - $screenHeight")
        if (myBitmap == null) return
        val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()
        val videoProportion = myBitmap!!.width.toFloat() / myBitmap!!.height.toFloat()

        if (videoProportion > screenProportion) {
            widthh = screenWidth
            heightt = (screenWidth.toFloat() / videoProportion).toInt()
        } else {
            widthh = (videoProportion * screenHeight.toFloat()).toInt()
            heightt = screenHeight
        }

        if (widthh > 0 && heightt > 0) {
            Lo.d("$widthh  --  $heightt")
            this.setMeasuredDimension(widthh, heightt)
            try {
                PhotorThread.getInstance().runBackground(object : PhotorThread.IBackground {
                    override fun doingBackground() {
                        mBitmap = Bitmap.createScaledBitmap(myBitmap!!, widthh, heightt, true)
                    }

                    override fun onCompleted() {
                        imageInterface?.loadBitmapSuccess()
                        invalidate()
                    }

                    override fun onCancel() {
                    }

                })
            } catch (e: OutOfMemoryError) {
            }


        }
    }


    override fun onDraw(canvas1: Canvas) {
        if (mBitmap == null) return
        this.canvas = canvas1
        canvas?.drawBitmap(mBitmap!!, 0f, 0f, paint)
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        rect = canvas?.clipBounds
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                timeTouchDown = System.currentTimeMillis()
            }

            MotionEvent.ACTION_MOVE -> {
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                if (System.currentTimeMillis() - timeTouchDown < 300) {
                    caculatorColor(ev)
                }
            }
        }
        return true
    }


    fun setColorDraw(color: Int) {
        if (isEraseMode) isEraseMode = false
        paint.color = color
    }

    fun actionBackStep() {
        if (current_step > 0) {
            val model = list[current_step - 1]
            current_step--
            drawColor(model.point, model.targetColor, model.sourceColor, completion = {

            })
            if (current_step == 0) {
                isBackAble = false
                imageInterface?.eventStateBack(false)
            }
            if (!isForwardAble) {
                isForwardAble = true
                imageInterface?.eventStateForward(true)
            }
        }
    }

    fun actionForwardStep() {
        if (current_step < total_step) {
            val model = list[current_step]
            drawColor(model.point, model.sourceColor, model.targetColor, null)
            current_step++
            if (current_step == total_step) {
                isForwardAble = false
                imageInterface?.eventStateForward(false)
            }

            if (!isBackAble) {
                isBackAble = true
                imageInterface?.eventStateBack(true)
            }
        }
    }

    fun actionErase() {
        isEraseMode = true
        paint.color = Color.WHITE
    }

    private fun caculatorColor(ev: MotionEvent) {
        isFocusable = false
        val mScaleFactor = 1f
        val mx = ev.x / mScaleFactor + rect!!.left
        val my = ev.y / mScaleFactor + rect!!.top
        rect = canvas?.clipBounds
        if (rect == null) return

        //private Point p1 = new Point();
        val p1 = Point()
        p1.x = mx.toInt()
        p1.y = my.toInt()
        if (mBitmap!!.height > my && mBitmap!!.width > mx) {
            if (my >= 0 && mx >= 0) {
                val sourceColor = mBitmap!!.getPixel(mx.toInt(), my.toInt())
                var targetColor = paint.color
                if (sourceColor == targetColor) {
                    targetColor = Color.WHITE
                }

                val model = com.volio.coloringbook2.java.DrawModel()
                model.point = p1
                model.sourceColor = sourceColor
                model.targetColor = targetColor

                //enable back
                isBackAble = true
                imageInterface?.eventStateBack(true)

                // disable forward
                isForwardAble = false
                imageInterface?.eventStateForward(false)

                drawColor(p1, sourceColor, targetColor, null)
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
        }
    }

    private fun drawColor(point: Point, sourceColor: Int, targetColor: Int, completion: (() -> Unit)?) {
        val myJob = CoroutineScope(Dispatchers.IO).launch {
            floodFill(mBitmap, point, sourceColor, targetColor)
            withContext(Dispatchers.Main) {
                invalidate()
            }
        }
        myJob.start()
    }


    private fun floodFill(bitmap: Bitmap?, point: Point?, i: Int, j: Int) {
        var point = point
        var j = j
        if (!isEditImage) isEditImage = true

        if (isForwardAble) {
            isForwardAble = false
        }

        val k = bitmap!!.width
        val l = bitmap.height

        pts.clear()
        if (i == j) {
            j = Color.WHITE
        }

        if (i != j) {
            val linkedlist = LinkedList<Point>()
            do {
                var i1 = point!!.x
                val j1: Int = point.y
                while (i1 > 0 && !isBlack(bitmap.getPixel(i1 - 1, j1), j)) {
                    i1--
                }

                var flag = false
                var flag1 = false
                while (i1 < k && !isBlack(bitmap.getPixel(i1, j1), j)) {//
                    bitmap.setPixel(i1, j1, j)

                    if (!flag && j1 > 0 && !isBlack(bitmap.getPixel(i1, j1 - 1), j)) {
                        linkedlist.add(Point(i1, j1 - 1))

                        flag = true
                    } else if (flag && isBlack(bitmap.getPixel(i1, j1 - 1), j)) {
                        flag = false
                    }
                    if (!flag1 && j1 < l - 1 && !isBlack(bitmap.getPixel(i1, j1 + 1), j)) {
                        linkedlist.add(Point(i1, j1 + 1))
                        flag1 = true
                    } else if (flag1 && j1 < l - 1 && isBlack(bitmap.getPixel(i1, j1 + 1), j)) {
                        flag1 = false
                    }
                    i1++
                }
                point = linkedlist.poll()
                pts.add(point)
            } while (point != null)
        }
    }


//    private fun isBlack(color: Int, j: Int): Boolean {
//        return Color.red(color) < 0x10 && Color.green(color) < 0x10 && Color.blue(color) < 0x10 || color == j
//    }


//    private fun isBlack(i: Int, j: Int): Boolean {
//        return Color.red(i) < blackValue && Color.blue(i) < blackValue && Color.green(i) < blackValue || i == j || Color.red(i) == Color.green(i) && Color.green(i) == Color.blue(i) && Color.red(i) < blackValue
//    }


    private val blackValue = 200

    private fun isBlack(i: Int, j: Int): Boolean {
        return Color.red(i) < blackValue && Color.blue(i) < blackValue && Color.green(i) < blackValue || i == j || Color.red(i) == Color.green(i) && Color.green(i) == Color.blue(i) && Color.red(i) < blackValue

//        return Color.red(i) == Color.green(i) && Color.green(i) == Color.blue(i) && Color.red(i) < 150 || i == j
    }

    private fun isColorEqual(r: Int, g: Int, b: Int, space: Int): Boolean {
        if (g >= r - space) {
            if (g <= r + space) {
                if (b >= r - space) {
                    if (b <= r + space) {
                        if (r <= 100) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

}