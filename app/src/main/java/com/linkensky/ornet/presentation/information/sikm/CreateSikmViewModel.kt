package com.linkensky.ornet.presentation.information.sikm

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.linkensky.ornet.data.model.RequestDataSIKM
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import org.koin.android.ext.android.inject


class CreateSikmViewModel(
    state: CreateSikmState,
    val service: SekitarKitaService
) : MvRxViewModel<CreateSikmState>(state) {

    init {
        getGorontaloArea()
        getOriginCities()
    }

    fun setRequestDataSIKM(data: RequestDataSIKM) = setState {
        copy(data = data)
    }

    fun getGorontaloArea() = viewModelScope.rxApi {
        service.gorontaloProvince().data
    }.execute { copy(gorontaloAreas = it) }

    fun getOriginCities() = viewModelScope.rxApi {
        service.originCities().data
    }.execute { copy(originCities = it) }

    fun setBottomSheet(number: Int) = setState { copy(sheetId = number) }

    fun setOriginText(value: String) = setState { copy(originText = value) }

    fun setDestinationText(value: String) = setState { copy(destinationText = value) }

    fun setOriginFilter(value: String) = setState { copy(originFilter = value) }

    fun setDestinationFilter(value: String) = setState { copy(destinationFilter = value) }

    companion object : MvRxViewModelFactory<CreateSikmViewModel, CreateSikmState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: CreateSikmState
        ): CreateSikmViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            return CreateSikmViewModel(state, service)
        }
    }
}