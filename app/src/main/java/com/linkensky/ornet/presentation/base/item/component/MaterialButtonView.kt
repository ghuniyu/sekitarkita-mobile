package com.linkensky.ornet.presentation.base.item.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.button.MaterialButton
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.ItemModel
import com.linkensky.ornet.presentation.base.item.KeyValue
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import com.linkensky.ornet.utils.*
import com.minjemin.android.core.item.ModelBind

class MaterialButtonView(context: Context) : FrameLayout(context),
    ModelBind<MaterialButtonView.Model> {
    private val button = MaterialButton(context)

    private val defaultLayout = LayoutOption.buildDefault()

    init {
        addView(
            button.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    override fun bind(model: Model) {
        button.apply {
            isEnabled = model.enabled
            text = model.text
            setOnClickListener(model.clickListener.getValue())
            isAllCaps = model.allCaps
            icon = model.icon
            elevation = 0f
            iconTint = R.color.grayLine.resColorTint()
            iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            backgroundTintList = model.background.resColorTint()
            setTextColor(model.textColor)
            setMargin(8.dp, 0.dp, 8.dp, 0.dp)

        }
        applyLayoutOption(model.layout, defaultLayout)
    }

    data class Model(
        var text: CharSequence = "",
        var clickListener: KeyValue<((View) -> Unit)?> = KeyValue(null),
        var enabled: Boolean = true,
        var allCaps: Boolean = true,
        var icon: Drawable? = null,
        var textColor: Int = android.R.color.white.resColor(),
        var background: Int = R.color.colorPrimary,
        val layout: LayoutOption = LayoutOption()
    ) : ItemModel<MaterialButtonView, Model>(::MaterialButtonView)
}