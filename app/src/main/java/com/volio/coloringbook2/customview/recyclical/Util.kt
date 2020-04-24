
package com.volio.coloringbook2.customview.recyclical

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.AttrRes
import com.volio.coloringbook2.R

internal fun Context.resolveDrawable(
    @AttrRes attr: Int? = null,
    fallback: Drawable? = null
): Drawable? {
    if (attr != null) {
        val a = theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            var d = a.getDrawable(0)
            if (d == null && fallback != null) {
                d = fallback
            }
            return d
        } finally {
            a.recycle()
        }
    }
    return fallback
}

internal fun View?.showOrHide(show: Boolean) {
    this?.visibility = if (show) VISIBLE else GONE
}

internal fun View?.onAttach(block: View.() -> Unit) {
    this?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View) = Unit

        override fun onViewAttachedToWindow(v: View) = v.block()
    })
}

internal fun View?.onDetach(block: View.() -> Unit) {
    this?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View) = v.block()

        override fun onViewAttachedToWindow(v: View) = Unit
    })
}

internal fun View?.makeBackgroundSelectable() {
    if (this != null && background == null) {
        background = context.resolveDrawable(R.attr.selectableItemBackground)
    }
}
