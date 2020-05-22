package com.linkensky.ornet.presentation.mac

import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.linkensky.ornet.*
import com.linkensky.ornet.presentation.base.BaseController

class MacAddressController : BaseController() {
    override fun buildModels() {
        carousel {
            padding(Carousel.Padding.dp(0, 16, 0, 16, 24))
            id("card-tutorial-carousel")
            models(
                1.rangeTo(5).map {
                    TutorialCardBindingModel_()
                        .id("tutorial-$it")
                        .title("$it. Cari menu About phone")
                        .text("Bisa dari menu bagian atas, atau lewat icon yang ada di menu utama")
                        .image(R.drawable.sample_tutorial)
                }
            )
            numViewsToShowOnScreen(1.2f)
        }
    }
}