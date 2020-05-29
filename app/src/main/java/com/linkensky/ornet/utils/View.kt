package com.linkensky.ornet.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.LayoutRes
import androidx.core.widget.doOnTextChanged


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

fun View.hideKeyboard(inputMethodManager: InputMethodManager) {
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
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

fun EditText.validate(message: String, validator: (String) -> Boolean) {
    this.doOnTextChanged { text, _, _, _ ->
        this.error = if (validator(text.toString())) null else message
    }

    this.error = if (validator(this.text.toString())) null else message
}

fun String.validPhone() = this.matches(Regex("^(^\\+62\\s?|^0)(\\d{3,4}-?){2}\\d{3,4}\$"))


fun EditText.required(
    error: String,
    fulfil: (() -> Unit)
): String {
    if (this.text.isNullOrBlank())
        this.error = error
    else
        fulfil()
    return this.text.toString()
}