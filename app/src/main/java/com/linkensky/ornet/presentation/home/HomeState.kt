package com.linkensky.ornet.presentation.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.*
import com.orhanobut.hawk.Hawk

data class HomeState(
    val indonesiaStatistics : Async<List<Country>> = Uninitialized,
    val banners : Async<List<Banner>> = Uninitialized,
    val gorontaloStatistics : Async<GtoStatisticResponse> = Uninitialized,
    val historyInteraction: Async<InteractionHistoryResponse> = Uninitialized,
    val zone: Char = Hawk.get(Const.STORAGE_LASTKNOWN_ZONE, 'g'),
    val location: String = Hawk.get(Const.STORAGE_LASTKNOWN_ADDRESS, Address()).location()
): MvRxState
