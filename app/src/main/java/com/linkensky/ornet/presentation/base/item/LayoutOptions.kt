package com.linkensky.ornet.presentation.base.item

import android.view.View
import android.view.ViewGroup
import com.linkensky.ornet.utils.dp
import com.linkensky.ornet.utils.lparams
import com.linkensky.ornet.utils.setMargin


data class LayoutOption(
    var padding: Frame? = null,
    var margin: Frame? = null,
    var lparams: Param? = null,
    var background: Color? = null
) {
    companion object {
        fun buildDefault() = LayoutOption(
            lparams = Param(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
            margin = Frame(0.dp),
            padding = Frame(0.dp)
        )
    }
}

fun layout(option: LayoutOption.() -> Unit) = LayoutOption().apply { this.option() }

fun View.applyLayoutOption(option: LayoutOption, default: LayoutOption? = null) {
    default?.let {
        applyLayoutOption(default)
    }

    option.padding?.let {
        setPadding(it.left, it.top, it.right, it.bottom)
    }
    option.margin?.let {
        setMargin(it.left, it.top, it.right, it.bottom)
    }
    option.lparams?.let {
        if (it.weight != null) {
            lparams(it.width, it.height, it.weight)
        } else {
            lparams(it.width, it.height)
        }
    }
    option.background?.let {
        setBackgroundColor(it.color)
    }
}
