package com.linkensky.ornet.utils

import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.linkensky.ornet.App
import kotlin.math.floor
import kotlin.math.roundToInt

fun Int.resString() = App.getContext().getString(this)

fun Int.resColor() = ContextCompat.getColor(App.getContext(), this)

fun Int.resColorTint() = ContextCompat.getColorStateList(App.getContext(), this)

fun Int.resDrawable() = ContextCompat.getDrawable(App.getContext(), this)

inline val Int.dp: Int
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            App.getContext().resources.displayMetrics
        ).roundToInt()
    }


val Int.sp: Int get() = if (this == 0) 0 else floor(App.AppConfig.fontDensity * this.toDouble()).toInt()

val Float.sp: Float get() = if (this == 0f) 0f else floor(App.AppConfig.fontDensity * this.toDouble()).toFloat()

val Float.spInt: Int get() = this.sp.toInt()
