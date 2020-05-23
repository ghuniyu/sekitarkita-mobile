package com.linkensky.ornet.presentation.statistic

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.linkensky.ornet.data.BaseCollectionResponse
import com.linkensky.ornet.data.Hospital
import com.linkensky.ornet.data.Province

data class StatisticState(
    val provinceStatistics: Async<List<Province>> = Uninitialized,
    val hospitals: Async<List<Hospital>> = Uninitialized
) : MvRxState