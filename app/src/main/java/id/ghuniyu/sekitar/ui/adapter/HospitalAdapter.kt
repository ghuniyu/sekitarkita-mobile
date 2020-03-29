package id.ghuniyu.sekitar.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.model.Hospital

class HospitalAdapter(layoutRes: Int, data: List<Hospital>) :
    BaseQuickAdapter<Hospital, BaseViewHolder>(layoutRes, data) {

    override fun convert(helper: BaseViewHolder, item: Hospital) {
        helper.setText(R.id.title, item.name)
        helper.setText(R.id.subtitle, item.address)
        helper.setText(R.id.area, item.area)
        helper.setText(R.id.phone, item.phone)
            .addOnClickListener(R.id.phone)
        helper.addOnClickListener(R.id.imageMap)
    }
}