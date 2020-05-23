package com.linkensky.ornet.presentation.information

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.data.services.PublicService
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import org.koin.android.ext.android.inject

class InformationViewModel(
    state: InformationState,
    private val service: SekitarKitaService,
    private val publicService: PublicService
) : MvRxViewModel<InformationState>(state) {

    init {
        getProvinces()
    }

    fun getProvinces() = viewModelScope.rxApi {
        publicService.getProvinces()
    }.execute { copy(provinceStatistics= it) }

    fun getHospitals() = viewModelScope.rxApi {
        service.getHospitals().data
    }.execute { copy(hospitals = it) }

    companion object : MvRxViewModelFactory<InformationViewModel, InformationState> {
        override fun create(viewModelContext: ViewModelContext, state: InformationState): InformationViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            val publicService: PublicService by viewModelContext.activity.inject()
            return InformationViewModel(state, service, publicService)
        }
    }
}