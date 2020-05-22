package com.linkensky.ornet.presentation.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.data.services.PublicService
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import org.koin.android.ext.android.inject

class HomeViewModel(
    state: HomeState,
    val service: SekitarKitaService,
    private val publicService: PublicService
) : MvRxViewModel<HomeState>(state) {

    init {
        getIndonesiaStatistics()
    }

    fun getIndonesiaStatistics() = viewModelScope.rxApi {
       publicService.getIndonesia()
    }.execute {
        copy(indonesiaStatistics = it)
    }


    companion object : MvRxViewModelFactory<HomeViewModel, HomeState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeState): HomeViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            val publicService: PublicService by viewModelContext.activity.inject()
            return HomeViewModel(state, service, publicService)
        }
    }
}