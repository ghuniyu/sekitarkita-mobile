package com.linkensky.ornet.presentation.base

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import com.airbnb.epoxy.EpoxyController

@BindingAdapter("android:layout_marginStart")
fun setMarginStart(view: View, value: Int) {
    val lp = view.layoutParams as ViewGroup.MarginLayoutParams
    lp.setMargins(value, lp.topMargin, lp.rightMargin, lp.bottomMargin)
    view.layoutParams = lp
}

@BindingAdapter("android:layout_marginEnd")
fun setMarginEnd(view: View, value: Int) {
    val lp = view.layoutParams as ViewGroup.MarginLayoutParams
    lp.setMargins(lp.leftMargin, lp.topMargin, value, lp.bottomMargin)
    view.layoutParams = lp
}

abstract class BaseController : EpoxyController()