package com.linkensky.ornet.presentation.selfcheck

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.linkensky.ornet.data.services.PublicService
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
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
        setState { copy(page = state.page + 1) }
    }

    fun prevPage() = withState { state ->
        val i = state.page - 1
        if (i > -1)
            setState { copy(page = i) }
    }
}