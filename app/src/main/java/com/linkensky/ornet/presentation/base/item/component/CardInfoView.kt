package com.linkensky.ornet.presentation.base.item.component

import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.LayoutItemModel
import kotlinx.android.synthetic.main.card_info_item.view.*

data class CardInfoView(
   val titleText: String,
   val contentText: String
): LayoutItemModel(R.layout.card_info_item) {
    override fun binder(view: View) = with(view) {
        title.text = titleText
        content.text = contentText
    }
}