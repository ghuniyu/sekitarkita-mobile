package com.linkensky.ornet.presentation.base.item.component

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.ItemModel
import com.linkensky.ornet.presentation.base.item.KeyValue
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import com.linkensky.ornet.utils.lparams
import com.linkensky.ornet.utils.resColor
import com.minjemin.android.core.item.ModelBind

class ViewText (context: Context) : RelativeLayout(context), ModelBind<ViewText.Model> {
    val item = TextView(context)
    private val defaultLayout = LayoutOption.buildDefault()
    init {
        lparams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(item)
        item.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun bind(model: Model) {
        item.apply {
            setOnClickListener(model.clickListener.getValue())
            text = model.text
            isEnabled = model.enabled
            gravity = model.gravity
            model.let {
                if (it.isBold && it.isItalic)
                    setTypeface(item.typeface, Typeface.BOLD_ITALIC)
                else if (it.isBold)
                    setTypeface(item.typeface, Typeface.BOLD)
                else if (it.isItalic)
                    setTypeface(item.typeface, Typeface.ITALIC)
                else typeface = Typeface.DEFAULT
            }
            if (model.maxLines != null)
                maxLines = model.maxLines ?: 2

            textSize = model.textSize
            setTextColor(model.textColor)

        }
        item.applyLayoutOption(model.itemLayout)
        applyLayoutOption(model.layout, defaultLayout)
    }

    data class Model(
        var text: String? = "",
        var clickListener: KeyValue<((View) -> Unit)?> = KeyValue(null),
        var enabled: Boolean = true,
        var isBold: Boolean = false,
        var isItalic: Boolean = false,
        var textSize: Float = 12f,
        var maxLines: Int? = null,
        var textColor: Int = R.color.colorBlack.resColor(),
        var gravity: Int = CENTER_HORIZONTAL or CENTER_VERTICAL,
        val layout: LayoutOption = LayoutOption(),
        val itemLayout: LayoutOption = LayoutOption()
    ) : ItemModel<ViewText, Model>(::ViewText)
}