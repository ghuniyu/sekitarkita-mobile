package com.linkensky.ornet.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.LayoutRes


fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ViewGroup.contentRes(layoutId: Int): View {
    return inflate(layoutId, true)
}

fun View.setMargin(horizontal: Int, vertical: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        setMargins(horizontal, vertical, horizontal, vertical)
    }
}


fun View.setMargin(left: Int, top: Int, right: Int, bottom: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        setMargins(left, top, right, bottom)
    }
}

fun Context.attrDrawable(@AttrRes attr: Int): Drawable? {
    val attrs = intArrayOf(attr)
    val typedArray = obtainStyledAttributes(attrs)
    val drawable = typedArray.getDrawable(0)
    typedArray.recycle()
    return drawable
}

fun View.setPadding(horizontal: Int, vertical: Int) {
    setPadding(horizontal, vertical, horizontal, vertical)
}

fun View.visible(value: Boolean = true) {
    visibility = if (value) View.VISIBLE else View.GONE
}

fun <T : View> T.lparams(width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT) = apply {
    layoutParams = when (parent) {
        is FrameLayout -> {
            FrameLayout.LayoutParams(width, height)
        }
        is LinearLayout -> {
            LinearLayout.LayoutParams(width, height)
        }
        else -> {
            ViewGroup.MarginLayoutParams(width, height)
        }
    }
}

inline fun <T : View> T.lparams(
    width: Int = MATCH_PARENT,
    height: Int = WRAP_CONTENT,
    weight: Float,
    closure: LinearLayout.LayoutParams.() -> Unit = {}
) = apply {
    layoutParams = LinearLayout.LayoutParams(width, height, weight).apply { closure() }
}