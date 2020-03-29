package id.ghuniyu.sekitar.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.model.CallCenter
import id.ghuniyu.sekitar.data.model.Hospital

class CallCenterAdapter(layoutRes: Int, data: List<CallCenter>) :
    BaseQuickAdapter<CallCenter, BaseViewHolder>(layoutRes, data) {

    override fun convert(helper: BaseViewHolder, item: CallCenter) {
        helper.setText(R.id.title, item.area)
        helper.setText(R.id.subtitle, item.area_detail)
        helper.setText(R.id.area, if (!item.website.isNullOrEmpty()) item.website else "Tidak Ada")
            .addOnClickListener(R.id.area)
        helper.setText(R.id.phone, item.phone)
            .addOnClickListener(R.id.phone)
        helper.setVisible(R.id.imageMap, false)
    }
}