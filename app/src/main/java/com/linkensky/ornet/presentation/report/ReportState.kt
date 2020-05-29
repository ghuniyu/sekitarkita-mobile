package com.linkensky.ornet.presentation.report

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.linkensky.ornet.App
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.BaseResponse
import com.linkensky.ornet.data.model.enums.Status
import com.orhanobut.hawk.Hawk

data class ReportState(
    val phone: String? = Hawk.get<String>(Const.PHONE, null),
    val name: String? = Hawk.get<String>(Const.NAME, null),
    val travelHistory: String? = null,
    val status: Status = Status.HEALTHY,
    val responsePostChangeStatus: Async<BaseResponse> = Uninitialized
) : MvRxState