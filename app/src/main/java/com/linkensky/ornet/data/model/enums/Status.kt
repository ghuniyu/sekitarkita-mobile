package com.linkensky.ornet.data.model.enums

import androidx.annotation.DrawableRes
import com.linkensky.ornet.App
import com.linkensky.ornet.R

enum class Status(private val info: Int) {
    ODP(R.string.odp_desc),
    PDP(R.string.pdp_desc),
    POSITIVE(R.string.positive_desc),
    OTG(R.string.otg_desc),
    TRAVELER(R.string.traveler_desc),
    HEALTHY(R.string.healthy);

    override fun toString(): String {
        return App.getContext().getString(info)
    }

    @DrawableRes
    fun getDrawable(): Int {
        return when (this) {
            ODP -> R.drawable.illustration_odp
            PDP -> R.drawable.illustration_pdp
            POSITIVE -> R.drawable.ic_positive
            OTG -> R.drawable.illustration_otg
            TRAVELER -> R.drawable.illustration_traveler
            else -> R.drawable.ic_illustration_healthy
        }
    }
}