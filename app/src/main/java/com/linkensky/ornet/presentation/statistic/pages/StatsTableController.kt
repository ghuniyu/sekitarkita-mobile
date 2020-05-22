package com.linkensky.ornet.presentation.statistic.pages

import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.linkensky.ornet.itemStats
import com.linkensky.ornet.presentation.base.BaseController
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.statistic.StatisticViewModel
import kotlin.random.Random

class StatsTableController(private  val viewModel: StatisticViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) { state ->
        when(val response = state.provinceStatistics) {
            is Loading -> {

            }
            is Success -> {
                val data = response()
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
            else -> {

            }
        }
        Unit
    }

}