package com.linkensky.ornet.presentation.home

import com.linkensky.ornet.R
import com.linkensky.ornet.cardInfo
import com.linkensky.ornet.presentation.base.BaseController

class HomeController : BaseController() {

    data class Card(
        val title: String,
        val icon: Int,
        val bg: Int
    )

    override fun buildModels() {

        val cards = arrayOf(
            Card("Rumah Sakit", R.drawable.ic_medical, R.color.purple),
            Card("Call Center", R.drawable.ic_call_center, R.color.redLight),
            Card("Pencegahan", R.drawable.ic_medical, R.color.yellowLight)
        )

        cards.map {
            cardInfo {
                id("card-info-${it.title}")
                title(it.title)
                bg(it.bg)
                icon(it.icon)
                when (it) {
                    cards.first() -> marStart(64)
                    cards.last() -> {
                        marStart(16)
                        marEnd(64)
                    }
                    else -> marStart(16)
                }
            }
        }
    }
}