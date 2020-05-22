package com.linkensky.ornet.presentation.home

import androidx.navigation.findNavController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.linkensky.ornet.*
import com.linkensky.ornet.presentation.base.MvRxEpoxyController

class HomeController(private val viewModel: HomeViewModel) : MvRxEpoxyController() {

    override fun buildModels() = withState(viewModel) {state ->
        topBar {
            id("home-top-bar")
            text("SekitarKita")
            onInfoClick { view ->
                view.findNavController().navigate(R.id.action_homeFragment_to_macAddressFragment)
            }
        }

        greeting {
            id("greeting")
            greeting("Selamat Malam")
            name("Ghuniyu")
            zoneInfo("Anda sedang berada di Zona Hijau Covid-19")
        }

        header {
            id("confirm")
            text("Data Indonesia Terkini")
        }

        subHeader {
            id("confirm_info")
            text("Data ini disediakan oleh kawalcorona.com")
        }

        when (val response = state.indonesiaStatistics) {
            is Loading -> {
                stats {
                    id("stats")
                    recovered("...")
                    positive("...")
                    death("...")
                }
            }

            is Success -> {
                val data = response().first()
                stats {
                    id("stats")
                    recovered(data.sembuh)
                    positive(data.positif)
                    death(data.meninggal)
                }
            }
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
                    PartnerCardBindingModel_()
                        .id("partner-$it")
                        .name("Pemprov Gorontalo")
                        .image(R.drawable.logo5)
                }
            )
            numViewsToShowOnScreen(1.5f)
        }
    }
}