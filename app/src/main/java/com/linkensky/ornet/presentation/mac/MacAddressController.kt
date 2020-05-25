package com.linkensky.ornet.presentation.mac

import androidx.annotation.DrawableRes
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.linkensky.ornet.*
import com.linkensky.ornet.presentation.base.BaseController

data class Tutorial(
    val title: String,
    val text: String,
    @DrawableRes
    val image: Int
)

class MacAddressController : BaseController() {
    override fun buildModels() {
        val tutorials = arrayOf(
            Tutorial(
                "Tekan Lihat MAC",
                "Anda akan di arahkan ke setting",
                R.drawable.tutorial1
            ),
            Tutorial(
                "Cari Status",
                "Cari dan tekan tulisan Status",
                R.drawable.tutorial2
            ),
            Tutorial(
                "Catat Bluetooth MAC",
                "Catat alamat Bluetooth anda",
                R.drawable.tutorial3
            )
        )
        carousel {
            padding(Carousel.Padding.dp(0, 16, 0, 16, 24))
            id("card-tutorial-carousel")
            models(tutorials.mapIndexed { index, tutorial ->
                TutorialCardBindingModel_()
                    .id("tutorial-$index")
                    .title("${index + 1}. ${tutorial.title}")
                    .text(tutorial.text)
                    .image(tutorial.image)
            })
            numViewsToShowOnScreen(1.3f)
        }
    }
}