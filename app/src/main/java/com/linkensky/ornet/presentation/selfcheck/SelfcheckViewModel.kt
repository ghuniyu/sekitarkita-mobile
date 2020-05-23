package com.linkensky.ornet.presentation.selfcheck

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.data.services.PublicService
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import org.koin.android.ext.android.inject

class SelfcheckViewModel(
    state: SelfcheckState,
    val service: SekitarKitaService,
    private val publicService: PublicService
) : MvRxViewModel<SelfcheckState>(state) {

    companion object : MvRxViewModelFactory<SelfcheckViewModel, SelfcheckState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: SelfcheckState
        ): SelfcheckViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            val publicService: PublicService by viewModelContext.activity.inject()
            return SelfcheckViewModel(state, service, publicService)
        }
    }

    fun nextPage() = withState { state ->
        if (state.page != 5)
            setState { copy(page = state.page + 1) }
    }

    fun prevPage() = withState { state ->
        if (state.page != 1)
            setState { copy(page = state.page - 1) }
    }

    fun cough() = withState {
        setState { copy(hasCough = !it.hasCough) }
    }

    fun flu() = withState {
        setState { copy(hasFlu = !it.hasFlu) }
    }

    fun bD() = withState {
        setState { copy(hasBD = !it.hasBD) }
    }

    fun soreThroat() = withState {
        setState { copy(hasSoreThroat = !it.hasSoreThroat) }
    }

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
}