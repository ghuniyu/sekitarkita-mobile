package com.linkensky.ornet.presentation.information

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.linkensky.ornet.data.CallCenter
import com.linkensky.ornet.data.Hospital
import com.linkensky.ornet.data.Province

data class InformationState(
    val provinceStatistics: Async<List<Province>> = Uninitialized,
    val hospitals: Async<List<Hospital>> = Uninitialized,
    val callCenters: Async<List<CallCenter>> = Uninitialized
) : MvRxState