package com.linkensky.ornet.presentation.home

import androidx.annotation.DrawableRes
import androidx.navigation.findNavController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.linkensky.ornet.*
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.orhanobut.hawk.Hawk

data class Partner(
    val name: String,
    @DrawableRes
    val banner: Int
)

data class Zone(
    val name: String,
    val info: String,
    val radar: String
)
class HomeController(private val viewModel: HomeViewModel) : MvRxEpoxyController() {

    override fun buildModels() = withState(viewModel) { state ->
        val zones = hashMapOf(
            'r' to Zone("Merah","Anda Sedang di Zona Merah Covid-19", "lottie/radar-red.json"),
            'g' to Zone("Hijau","Anda Sedang di Zona Hijau Covid-19", "lottie/radar-green.json"),
            'y' to Zone("Kuning","Anda Sedang di Zona Kuning Covid-19", "lottie/radar-red.json")
        )
        val k = zones.keys.random()

        topBar {
            id("home-top-bar")
            text("SekitarKita")
            zone(k)
            onInfoClick { view ->
                view.findNavController().navigate(R.id.action_homeFragment_to_macAddressFragment)
            }
        }

        greeting {
            id("greeting")
            greeting("Selamat Malam")
            name(Hawk.get(Const.NAME, App.getContext().getString(R.string.anonym)))
            zoneInfo(zones[k]?.info)
            zone(k)
            onSelfcheck { view ->
                view.findNavController().navigate(R.id.action_homeFragment_to_selfcheckFragment)
            }
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
                    isLoading(true)
                }
            }

            is Success -> {
                val data = response().first()
                stats {
                    id("stats")
                    isLoading(false)
                    recovered(data.sembuh)
                    positive(data.positif)
                    death(data.meninggal)
                }
            }
        }

        header {
            id("partner")
            text("Kerja Sama")
        }

        subHeader {
            id("confirm_info")
            text("Saat ini Kami Bekerjasama dengan")
        }

        val partners = arrayOf(
            Partner("Pemprov. Gorontalo", R.drawable.pemprov_gto_banner),
            Partner("Ritase", R.drawable.ritase_banner),
            Partner("Juragankost", R.drawable.juragankost_banner)
        )

        carousel {
            padding(Carousel.Padding.dp(16, 16, 16, 16, 8))
            id("card-info-carousel")
            models(
                partners.map {
                    PartnerCardBindingModel_()
                        .id("partner-${it.name}")
                        .image(it.banner)
                }
            )
            numViewsToShowOnScreen(1.5f)
        }
    }
}

class BluetoothStateChanged(
    val isEnable: Boolean = false
)