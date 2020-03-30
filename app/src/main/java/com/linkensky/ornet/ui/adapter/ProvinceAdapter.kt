package com.linkensky.ornet.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.Province

class ProvinceAdapter (layoutRes: Int, data: List<Province>) : BaseQuickAdapter<Province, BaseViewHolder>(layoutRes, data){
    override fun convert(helper: BaseViewHolder, item: Province) {
        helper.setText(R.id.province, item.attributes.Provinsi)
        helper.setText(R.id.positive, item.attributes.Kasus_Posi.toString())
        helper.setText(R.id.recover, item.attributes.Kasus_Semb.toString())
        helper.setText(R.id.death, item.attributes.Kasus_Meni.toString())
    }
}