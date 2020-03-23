package id.ghuniyu.sekitar.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.model.Interaction
import id.ghuniyu.sekitar.utils.Formatter


class InteractionAdapter(layoutRes: Int, data: List<Interaction>) : BaseQuickAdapter<Interaction, BaseViewHolder>(layoutRes, data) {

    override fun convert(helper: BaseViewHolder, item: Interaction) {
        helper.setText(R.id.device_name, item.another_device)
        helper.setText(R.id.timestamps, Formatter.datetify(item.created_at))

        if (item.latitude != null && item.longitude != null) {
            helper.setVisible(R.id.has_location, true)
        }
    }
}