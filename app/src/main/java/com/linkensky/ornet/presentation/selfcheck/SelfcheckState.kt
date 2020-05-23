package com.linkensky.ornet.presentation.selfcheck

import com.airbnb.mvrx.MvRxState

data class SelfcheckState(
    val page: Int = 0
) : MvRxState