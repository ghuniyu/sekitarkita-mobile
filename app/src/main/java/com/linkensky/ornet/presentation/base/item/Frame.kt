package com.linkensky.ornet.presentation.base.item

import android.content.res.ColorStateList

/**
 * Used to store padding and margin
 */
data class Frame(var left: Int, var top: Int, var right: Int, var bottom: Int) {
    constructor(horizontal: Int, vertical: Int) : this(horizontal, vertical, horizontal, vertical)
    constructor(all: Int) : this(all, all, all, all)
}

data class Param(var width: Int, var height: Int, val weight: Float? = null)

data class Color(var color: Int)