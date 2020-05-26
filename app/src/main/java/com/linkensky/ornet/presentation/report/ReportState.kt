package com.linkensky.ornet.presentation.report

import com.airbnb.mvrx.MvRxState
import com.linkensky.ornet.App
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.enums.Status

data class ReportState(
    val phone: String? = null,
    val name: String? = null,
    val travelHistory: String? = null,
    val status: Status = Status.HEALTHY
) : MvRxState