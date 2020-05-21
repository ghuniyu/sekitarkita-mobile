package com.linkensky.ornet.presentation.home

import com.airbnb.epoxy.carousel
import com.linkensky.ornet.CardInfoBindingModel_
import com.linkensky.ornet.presentation.base.BaseController

class HomeController : BaseController() {

    override fun buildModels() {

        carousel {
            id("card-info")
            models(
                1.rangeTo(3).map {
                    CardInfoBindingModel_()
                        .id("card-info-$it")
                }
            )
        }
    }
}