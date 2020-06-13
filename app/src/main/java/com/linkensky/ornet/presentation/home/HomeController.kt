package com.linkensky.ornet.presentation.home

import android.view.Gravity
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.linkensky.ornet.*
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.presentation.base.item.component.MaterialButtonView
import com.linkensky.ornet.presentation.base.item.component.ViewText
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp
import com.linkensky.ornet.utils.resString
import com.linkensky.ornet.utils.sp
import com.orhanobut.hawk.Hawk
import java.util.*

data class Zone(
    val info: String,
    val radar: String
)

@BindingAdapter("android:url")
fun setImageURL(view: ImageView, url: String?) {
    Glide
        .with(view)
        .load(url)
        .into(view)
}

class HomeController(private val viewModel: HomeViewModel) : MvRxEpoxyController() {

    override fun buildModels() = withState(viewModel) { state ->

        val words = arrayOf(
            "Selalu Jaga Kesehatan yaa...",
            "Jangan lupa cuci tangan...",
            "Makan makanan bergizi ya...",
            "Jangan lupa tetap berolah raga...",
            "#Dirumahaja lebih baik..."
        )
        val zones = hashMapOf(
            'r' to Zone(R.string.zone_red.resString(), "lottie/radar-red.json"),
            'g' to Zone(R.string.zone_green.resString(), "lottie/radar-green.json"),
            'y' to Zone(R.string.zone_yellow.resString(), "lottie/radar-red.json"),
            'u' to Zone(words.random(), "lottie/radar-green.json")
        )
        val k = state.zone

        topBar {
            id("home-top-bar")
            text("SekitarKita")
            zone(k)
            onInfoClick { view ->
                view.findNavController().navigate(R.id.action_homeFragment_to_aboutFragment)
            }
        }

        var greeting = R.string.good_moring.resString()
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> {
                greeting = R.string.good_moring.resString()
            }
            in 12..15 -> {
                greeting = R.string.good_afternoon.resString()
            }
            in 16..20 -> {
                greeting = R.string.good_evening.resString()
            }
            in 21..23 -> {
                greeting = R.string.good_night.resString()
            }
        }

        greeting {
            id("greeting")
            greeting(greeting)
            name(Hawk.get(Const.NAME, R.string.anonym.resString()))
            zoneInfo(zones[k]?.info)
            zone(k)
            onSelfcheck { view ->
                view.findNavController().navigate(R.id.action_homeFragment_to_selfcheckFragment)
            }
        }

        locationInfo {
            id("location")
            address(state.location)
        }

        if (Hawk.get(Const.AREA_NAME, "") == Const.AREA_GORONTALO) {
            header {
                id("confirm")
                text("Data Gorontalo Terkini")
            }

            subHeader {
                id("confirm_info_gorontalo")
                text("Data ini disediakan oleh Dinkes Gorontalo")
            }

            when (val response = state.gorontaloStatistics) {
                is Loading -> {
                    gtoStats {
                        id("stats-loading-gorontalo")
                        isLoading(true)
                    }
                }

                is Success -> {
                    val data = response().data
                    if (data.size == 3) {
                        val treatment = data[2].statuses[0].orangs_count
                        val recover = data[2].statuses[1].orangs_count
                        val death = data[2].statuses[2].orangs_count
                        gtoStats {
                            id("stats-gorontalo")
                            isLoading(false)
                            positive((treatment + recover + death).toString())
                            treatment(treatment.toString())
                            recover(recover.toString())
                            death(death.toString())
                        }
                    }
                }

                is Fail -> {
                    addModel(
                        "stas-fail-text-gorontalo",
                        ViewText.Model(
                            text = R.string.something_went_wrong.resString(),
                            gravity = Gravity.CENTER,
                            layout = LayoutOption(margin = Frame(8.dp, 32.dp, 8.dp, 0.dp)),
                            textSize = 13f.sp
                        )
                    )
                    addModel(
                        "stas-fail-gorontalo",
                        MaterialButtonView.Model(
                            text = R.string.refresh.resString(),
                            clickListener = keyValue { _ -> viewModel.getIndonesiaStatistics() },
                            allCaps = false,
                            layout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 32.dp))
                        )
                    )
                }
            }
        } else {
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

                is Fail -> {
                    addModel(
                        "stas-fail-text",
                        ViewText.Model(
                            text = R.string.something_went_wrong.resString(),
                            gravity = Gravity.CENTER,
                            layout = LayoutOption(margin = Frame(8.dp, 32.dp, 8.dp, 0.dp)),
                            textSize = 13f.sp
                        )
                    )
                    addModel(
                        "stas-fail",
                        MaterialButtonView.Model(
                            text = R.string.refresh.resString(),
                            clickListener = keyValue { _ -> viewModel.getIndonesiaStatistics() },
                            allCaps = false,
                            layout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 32.dp))
                        )
                    )
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

        when (val banners = state.banners) {
            is Loading -> addModel(
                "banner-loading",
                LottieLoading(
                    layout = LayoutOption(
                        margin = Frame(
                            right = 8.dp,
                            left = 8.dp,
                            top = 80.dp,
                            bottom = 0.dp
                        )
                    )
                )
            )

            is Success -> carousel {
                padding(Carousel.Padding.dp(16, 16, 16, 16, 8))
                id("card-info-carousel")
                models(
                    banners().mapIndexed { i, banner ->
                        PartnerCardBindingModel_()
                            .id("partner-$i")
                            .image("${BuildConfig.APP_IMAGE_URL}${banner.logo}")
                    }
                )
                numViewsToShowOnScreen(1.5f)
            }
        }
    }
}