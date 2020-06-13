package com.linkensky.ornet.presentation.base.item.component

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.ItemModel
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.ModelBind
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import com.linkensky.ornet.utils.lparams
import com.linkensky.ornet.utils.resColor
import com.linkensky.ornet.utils.setMargin

class DividerView(context: Context) : FrameLayout(context), ModelBind<DividerView.Model> {
    val item = View(context)

    init {
        addView(item)
    }

    override fun bind(model: Model) {
        item.apply {
            isEnabled = model.enabled
            setBackgroundColor(model.color)
        }
        lparams(ViewGroup.LayoutParams.MATCH_PARENT, model.height)
        if (model.hasMargin) setMargin(MARGIN_HORINZONTAL, MARGIN_VERTICAL)
        applyLayoutOption(model.layout)
    }

    data class Model(
        var enabled: Boolean = true,
        var hasMargin : Boolean = false,
        var color: Int = R.color.colorWhiteGray.resColor(),
        var height: Int = LAYOUT_HEIGHT,
        val layout: LayoutOption = LayoutOption()
    ) : ItemModel<DividerView, Model>(::DividerView)

    companion object {
        const val LAYOUT_HEIGHT = 20
        const val MARGIN_HORINZONTAL = 24
        const val MARGIN_VERTICAL = 0
    }
}