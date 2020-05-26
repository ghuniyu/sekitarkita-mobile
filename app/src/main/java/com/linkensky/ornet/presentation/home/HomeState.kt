package com.linkensky.ornet.presentation.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.linkensky.ornet.data.model.Country
import com.linkensky.ornet.data.model.InteractionHistoryResponse

data class HomeState(
    val indonesiaStatistics : Async<List<Country>> = Uninitialized,
    val historyInteraction: Async<InteractionHistoryResponse> = Uninitialized
): MvRxState
