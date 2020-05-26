package com.linkensky.ornet.presentation.selfcheck

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
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

    fun nextPage() = setState { copy(page = this.page + (if (this.page != 6) 1 else 0)) }
    fun prevPage() = setState { copy(page = this.page - (if (this.page != 1) 1 else 0)) }
    fun cough() = setState { copy(hasCough = !this.hasCough) }

    fun flu() = setState { copy(hasFlu = !this.hasFlu) }

    fun bD() = setState { copy(hasBreathProblem = !this.hasBreathProblem) }

    fun soreThroat() = setState { copy(hasSoreThroat = !this.hasSoreThroat) }

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
}