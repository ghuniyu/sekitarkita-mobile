package com.linkensky.ornet.presentation.report

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.data.model.ChangeStatusRequest
import com.linkensky.ornet.data.model.enums.Status
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import org.koin.android.ext.android.inject

class ReportViewModel(
    state: ReportState,
    private val service: SekitarKitaService
) : MvRxViewModel<ReportState>(state) {

    companion object : MvRxViewModelFactory<ReportViewModel, ReportState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: ReportState
        ): ReportViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            return ReportViewModel(state, service)
        }
    }

    fun setPhone(phone: String) = setState { copy(phone = phone) }
    fun setName(name: String) = setState { copy(name = name) }
    fun setTravelHistory(travelHistory: String) = setState { copy(travelHistory = travelHistory) }
    fun setStatus(status: Status) = setState { copy(status = status) }

    fun postChangeStatus(request: ChangeStatusRequest) = viewModelScope.rxApi {
        service.postChangeStatus(request)
    }.execute {
        copy(responsePostChangeStatus = it)
    }
}