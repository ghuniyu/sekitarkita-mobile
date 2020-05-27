package com.linkensky.ornet.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.google.gson.Gson
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.InteractionHistoryRequest
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.data.socket
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import com.orhanobut.hawk.Hawk
import io.socket.client.IO
import io.socket.client.Socket
import org.koin.android.ext.android.inject
import org.koin.experimental.property.inject
import org.koin.java.KoinJavaComponent.inject

class HomeViewModel(
    state: HomeState,
    val service: SekitarKitaService,
    val socketClient: Socket
) : MvRxViewModel<HomeState>(state) {

    init {
        getIndonesiaStatistics()
    }

    fun getIndonesiaStatistics() = viewModelScope.rxApi {
        service.getIndonesia()
    }.execute {
        copy(indonesiaStatistics = it)
    }

    fun getDeviceInteractionHistory(deviceId: String) = viewModelScope.rxApi {
        service.deviceHistories(InteractionHistoryRequest(deviceId))
    }.execute {
        copy(historyInteraction = it)
    }

    fun listenZoneStatus() {
//        socketClient.connect()
//        socketClient.on(Socket.EVENT_CONNECT) {
//
//        }
//        socketClient.on("sekitar-device-${Hawk.get<String>(Const.DEVICE_ID)}") {
//            Log.d("HELLO", it.first().toString())
//        }
    }


    companion object : MvRxViewModelFactory<HomeViewModel, HomeState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: HomeState
        ): HomeViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            val socketClient: Socket = IO.socket("http://localhost:4000")
            return HomeViewModel(state, service, socketClient)
        }
    }
}