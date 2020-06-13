package com.linkensky.ornet.presentation.base.item.component

import android.text.TextUtils
import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.*
import com.linkensky.ornet.utils.visible
import kotlinx.android.synthetic.main.text_with_right_icon_item.view.*

data class ListTileView(
    val icon: Int = R.drawable.ic_keyboard_arrow_right,
    val title: String = "Title",
    val subtitle: String? = null,
    val layout: LayoutOption = LayoutOption(),
    val onClick: UnitListener? = keyValue(null)
) : LayoutItemModel(R.layout.text_with_right_icon_item) {
    override fun binder(view: View) = with(view) {
        applyLayoutOption(layout, LayoutOption.buildDefault())
        icon_place.setImageResource(icon)
        title_place.text = title
        subtitle_place.visible(subtitle?.isNotEmpty() ?: false)
        subtitle_place.text = subtitle
        subtitle_place.maxLines = 2
        subtitle_place.ellipsize = TextUtils.TruncateAt.END
        view.setOnClickListener {
            onClick?.getValue()?.invoke()
        }
    }
}