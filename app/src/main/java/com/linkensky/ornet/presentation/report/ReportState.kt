package com.linkensky.ornet.presentation.report

import com.airbnb.mvrx.MvRxState
import com.linkensky.ornet.App
import com.linkensky.ornet.R

enum class Status(private val status: Int) {
    ODP(R.string.odp_desc),
    PDP(R.string.pdp_desc),
    POSITIVE(R.string.positive_desc),
    OTG(R.string.otg_desc),
    TRAVELER(R.string.traveler_desc),
    HEALTHY(R.string.healthy);

    override fun toString(): String {
        return App.getContext().getString(status)
    }
}

data class ReportState(
    val phone: String? = null,
    val name: String? = null,
    val travelHistory: String? = null,
    val status: Status = Status.HEALTHY
) : MvRxState