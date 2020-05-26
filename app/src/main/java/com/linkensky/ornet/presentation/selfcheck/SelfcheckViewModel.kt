package com.linkensky.ornet.presentation.selfcheck

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.data.model.RequestReportData
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import org.koin.android.ext.android.inject

class SelfcheckViewModel(
    state: SelfcheckState,
    val service: SekitarKitaService
) : MvRxViewModel<SelfcheckState>(state) {

    companion object : MvRxViewModelFactory<SelfcheckViewModel, SelfcheckState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: SelfcheckState
        ): SelfcheckViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            return SelfcheckViewModel(state, service)
        }
    }

    fun nextPage() = setState {
        var currentPage = page
        if (currentPage != 6) currentPage += 1
        copy(page = currentPage)
    }

    fun prevPage() = setState {
        var currentPage = page
        if (currentPage != 1) currentPage -= 1
        copy(page = currentPage)
    }

    fun cough() =  setState { copy(hasCough = !hasCough) }

    fun flu() = setState { copy(hasFlu = !hasFlu) }

    fun bD() = setState { copy(hasBreathProblem = !hasBreathProblem) }

    fun soreThroat() = setState { copy(hasSoreThroat = !hasSoreThroat) }

    fun hasFever(value: Boolean) {
        setState { copy(hasFever = value) }
        nextPage()
    }

    fun inInfectedCountry(value: Boolean) {
        setState { copy(inInfectedCountry = value) }
        nextPage()
    }

    fun inInfectedCity(value: Boolean) {
        setState { copy(inInfectedCity = value) }
        nextPage()
    }

    fun directContact(value: Boolean) {
        setState { copy(directContact = value) }
        nextPage()
    }

    fun setPhone(phone: String) = setState { copy(phone = phone) }
    fun setName(name: String) = setState { copy(name = name) }

    fun storeReportTest(resultTest: RequestReportData) = viewModelScope.rxApi {
        service.storeSelfCheck(resultTest)
    }.execute {
        copy(responseStoreTest = it)
    }

    fun clearAllState() = setState {
        SelfcheckState()
    }
}