package com.linkensky.ornet.presentation.selfcheck

import com.airbnb.mvrx.MvRxState

data class SelfcheckState(
    val page: Int = 1,

    val hasFever: Boolean = false,
    val hasFlu: Boolean = false,
    val hasCough: Boolean = false,
    val hasBD: Boolean = false,
    val hasSoreThroat: Boolean = false,
    val inInfectedCountry: Boolean = false,
    val inInfectedCity: Boolean = false,
    val directContact: Boolean = false
) : MvRxState