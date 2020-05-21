package com.linkensky.ornet.presentation.home

import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.linkensky.ornet.*
import com.linkensky.ornet.presentation.base.BaseController

class HomeController : BaseController() {
    override fun buildModels() {
        topBar {
            id("sekitar-header")
            text("SekitarKita")
        }

        greeting {
            id("greeting")
            greeting("Selamat Malam")
            name("Ghuniyu")
            zoneInfo("Anda sedang berada di Zona Hijau Covid-19")
        }

        header {
            id("confirm")
            text("Terkini")
        }

        subHeader {
            id("confirm_info")
            text("Data ini disediakan oleh kawalcorona.com")
        }

        carousel {
            padding(Carousel.Padding.dp(16, 16, 16, 0, 8))
            hasFixedSize(true)
            id("card-info-carousel")
            models(
                1.rangeTo(5).map {
                    CardInfoBindingModel_()
                        /*.marStart(if (it == 1) 32 else 16)
                        .marEnd(if (it == 5) 32 else 0)*/
                        .icon(R.drawable.ic_medical)
                        .id("info-$it")
                        .bg(R.color.greenZone)
                        .title("Info $it")
                }
            )
        }

        selfCheck {
            id("self-check")
        }

        header {
            id("partner")
            text("Kerja Sama")
        }

        subHeader {
            id("confirm_info")
            text("Saat ini Kami Bekerjasama dengan")
        }

        carousel {
            padding(Carousel.Padding.dp(16, 16, 16, 16, 8))
            id("card-info-carousel")
            models(
                1.rangeTo(5).map {
                    CardInfoBindingModel_()
                        .icon(R.drawable.ic_medical)
                        .id("info-$it")
                        .bg(R.color.greenZone)
                        .title("Info $it")
                }
            )
            numViewsToShowOnScreen(1.5f)
        }
    }
}