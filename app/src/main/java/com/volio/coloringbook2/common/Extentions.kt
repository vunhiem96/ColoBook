package com.volio.coloringbook2.common

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
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

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun ImageView.setImageTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

