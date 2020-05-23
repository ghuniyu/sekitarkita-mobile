package com.linkensky.ornet.presentation.statistic.pages

import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.linkensky.ornet.itemStats
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.presentation.statistic.StatisticViewModel
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp

class StatsTableController(private val viewModel: StatisticViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) { state ->
        when (val response = state.provinceStatistics) {
            is Loading -> {
                addModel(
                    "loading-provinces",
                    LottieLoading(layout = LayoutOption(margin = Frame(8.dp, 80.dp)))
                )
            }
            is Success -> {
                val data = response.invoke()
                data.mapIndexed { index, it ->
                    itemStats {
                        id("stats-${it.attributes.FID}")
                        area(it.attributes.Provinsi)
                        even(index % 2 == 0)
                        positive("${it.attributes.Kasus_Posi}")
                        recover("${it.attributes.Kasus_Semb}")
                        death("${it.attributes.Kasus_Meni}")
                    }
                }
            }
        }
        Unit
    }

}