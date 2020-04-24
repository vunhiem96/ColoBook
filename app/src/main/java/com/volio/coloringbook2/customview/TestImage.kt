package com.volio.coloringbook2.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.interfaces.ImageInterface
import com.volio.coloringbook2.java.Lo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class TestImage : ImageView {
    private var mBitmap: Bitmap? = null
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

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 2f
        paint.isDither = true
        paint.color = Color.parseColor("#a6f914")
        paint.isAntiAlias = false
    }

    fun setListener(imageInterface: ImageInterface) {
        this.imageInterface = imageInterface
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

    fun getBitmap(): Bitmap? {
        return mBitmap
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
                    dao.updatePercentImage(imagename, percent)
                }
            } else {
                dao.updatePercentImage(imagename, 0)
            }
        }
    }

    fun setPixelWhite(white: Int) {
        pixelWhite = white
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


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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


    fun createBitMap(bt: Bitmap) {
        Lo.d("dsk", bt.toString())
        mBitmap = bt.copy(bt.config, true)
        setImageBitmap(mBitmap)
    }


    fun update() {
        setMeasuredDimension(measuredWidth,
            drawable.intrinsicHeight * measuredWidth / drawable.intrinsicWidth)
    }

    fun fillColor(x: Int, y: Int) {
        Lo.d("fill color x,y: $x , $y")
//        rect = canvas?.clipBounds
        caculatorColor(x, y)

    }


    private fun caculatorColor(mx: Int, my: Int) {

        val p1 = Point()
        p1.x = mx
        p1.y = my
        if (mBitmap!!.height > my && mBitmap!!.width > mx) {
            if (my >= 0 && mx >= 0) {
                val sourceColor = mBitmap!!.getPixel(mx, my)
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

    private val blackValue = 70

    private fun isBlack(i: Int, j: Int): Boolean {

        val red = Color.red(i)
        val green = Color.green(i)
        val blue = Color.blue(i)
        return red < blackValue && blue < blackValue && green < blackValue || i == j || isColorEqual(red, green, blue, 10)
    }

    private fun isColorEqual(r: Int, g: Int, b: Int, space: Int): Boolean {
        if (g >= r - space) {
            if (g <= r + space) {
                if (b >= r - space) {
                    if (b <= r + space) {
                        if (r <= 200) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }


}