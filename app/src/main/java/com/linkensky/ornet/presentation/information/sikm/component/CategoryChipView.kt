package com.linkensky.ornet.presentation.information.sikm.component

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.chip.Chip
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.ItemModel
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.ModelBind
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import com.linkensky.ornet.utils.dp
import com.linkensky.ornet.utils.lparams

class CategoryChipView(context: Context) : LinearLayout(context),
    ModelBind<CategoryChipView.Model> {
    private val leftChip = Chip(ContextThemeWrapper(context, R.style.Widget_MaterialComponents_Chip_Choice))
    private val rightChip = Chip(ContextThemeWrapper(context, R.style.Widget_MaterialComponents_Chip_Choice))
    private val space = Space(context).lparams(4.dp, 0.dp)

    init {
        orientation = HORIZONTAL
        setPadding(8.dp, 0.dp, 8.dp, 0.dp)
        lparams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(leftChip.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(space)
        addView(rightChip.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    }

    override fun bind(model: Model) {
        model.rightChip?.invoke(rightChip)
        model.leftChip?.invoke(leftChip)
        applyLayoutOption(model.layout)
    }

    data class Model(
        var rightChip: ((chip: Chip) -> Unit)? = null,
        var leftChip: ((chip: Chip) -> Unit)? = null,
        var layout: LayoutOption = LayoutOption()
    ) : ItemModel<CategoryChipView, Model>(::CategoryChipView)
}