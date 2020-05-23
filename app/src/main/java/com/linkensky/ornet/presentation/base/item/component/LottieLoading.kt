package com.linkensky.ornet.presentation.base.item.component

import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.LayoutItemModel
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption

data class LottieLoading(
    val layout: LayoutOption = LayoutOption.buildDefault()
): LayoutItemModel(R.layout.lottie_loading) {
    override fun binder(view: View) {
       view.applyLayoutOption(layout)
    }
}