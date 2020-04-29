package com.volio.coloringbook2.common

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.ImageViewCompat


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun gg(tag:String, bien:String){
    Log.i(tag,bien)
}
fun toast(context:Context,toast:String){
    Toast.makeText(context,toast,Toast.LENGTH_LONG).show()
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun ImageView.setImageTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

