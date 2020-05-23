package com.linkensky.ornet.presentation.base.item.component

import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.LayoutItemModel
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import com.linkensky.ornet.utils.resString
import kotlinx.android.synthetic.main.lottie_empty_state.view.*

data class LottieEmptyState(
    val layout: LayoutOption = LayoutOption.buildDefault(),
    val text: String = R.string.empty_data.resString()
): LayoutItemModel(R.layout.lottie_empty_state) {
    override fun binder(view: View) = with(view){
        applyLayoutOption(layout)
        titleText.text = text
    }
}