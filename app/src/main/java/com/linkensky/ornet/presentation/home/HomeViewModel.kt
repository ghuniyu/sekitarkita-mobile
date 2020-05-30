package com.linkensky.ornet.presentation.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.Address
import com.linkensky.ornet.data.model.Device
import com.linkensky.ornet.data.model.Interaction
import com.linkensky.ornet.data.model.InteractionHistoryRequest
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject

class HomeViewModel(
    state: HomeState,
    private val service: SekitarKitaService
) : MvRxViewModel<HomeState>(state) {

    init {
        if (Hawk.get(Const.AREA_NAME, "") == Const.AREA_GORONTALO)
            getGorontaloStatistic()
        else
            getIndonesiaStatistics()

        getBanner()
    }

    private fun getBanner() = viewModelScope.rxApi {
        service.getBanner().data
    }.execute {
        copy(banners = it)
    }

    fun getIndonesiaStatistics() = viewModelScope.rxApi {
        service.getIndonesia()
    }.execute {
        copy(indonesiaStatistics = it)
    }

    private fun getGorontaloStatistic() = viewModelScope.rxApi {
        service.getGorontalo()
    }.execute {
        copy(gorontaloStatistics = it)
    }

    fun getDeviceInteractionHistory(deviceId: String) = viewModelScope.rxApi {
        service.deviceHistories(InteractionHistoryRequest(deviceId))
    }.execute {
        copy(historyInteraction = it)
    }

    fun updateZone() = setState {
        copy(zone = Hawk.get(Const.STORAGE_LASTKNOWN_ZONE, 'g'))
    }

    fun updateLocation() = setState {
        val address = Hawk.get(Const.STORAGE_LASTKNOWN_ADDRESS, Address())
        address.province?.let { province ->
            when {
                province.toLowerCase().contains(Const.AREA_GORONTALO) -> {
                    Hawk.put(Const.AREA_NAME, Const.AREA_GORONTALO)
                    if(!gorontaloStatistics.complete) getGorontaloStatistic()
                }
                else -> {
                    Hawk.put(Const.AREA_NAME, province)
                    if(!indonesiaStatistics.complete) getIndonesiaStatistics()
                }
            }
        }

        copy(location = address.location())
    }

    fun setInteraction(interaction: Interaction) = setState {
        copy(interaction = interaction)
    }

    companion object : MvRxViewModelFactory<HomeViewModel, HomeState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeState): HomeViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            return HomeViewModel(state, service)
        }
    }
}