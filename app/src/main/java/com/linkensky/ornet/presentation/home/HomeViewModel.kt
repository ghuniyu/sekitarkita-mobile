package com.linkensky.ornet.presentation.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.Address
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
        if (Hawk.get<String>(Const.AREA_NAME, null) == "gorontalo") {
            getGorontaloStatistic()
        } else
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
        copy(location = Hawk.get(Const.STORAGE_LASTKNOWN_ADDRESS, Address()).location())
    }


    companion object : MvRxViewModelFactory<HomeViewModel, HomeState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeState): HomeViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            return HomeViewModel(state, service)
        }
    }
}