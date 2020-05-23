package com.linkensky.ornet.presentation.base.item.component

import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.KeyValue
import com.linkensky.ornet.presentation.base.item.LayoutItemModel
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import kotlinx.android.synthetic.main.lottie_error_state.view.*

data class LottieErrorState(
    val layout: LayoutOption = LayoutOption.buildDefault(),
    var clickListener: KeyValue<((View) -> Unit)?> = KeyValue(null)
    ): LayoutItemModel(R.layout.lottie_error_state) {
    override fun binder(view: View) = with(view){
        applyLayoutOption(layout)
        buttonRefresh.setOnClickListener(clickListener.getValue())
    }
}