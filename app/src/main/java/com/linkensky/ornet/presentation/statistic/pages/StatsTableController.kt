package com.linkensky.ornet.presentation.statistic.pages

import com.linkensky.ornet.itemStats
import com.linkensky.ornet.presentation.base.BaseController
import kotlin.random.Random

class StatsTableController : BaseController() {
    override fun buildModels() {
        1.rangeTo(100).forEach { i ->
            itemStats {
                id("stats-$i")
                area("DKI Jakarta")
                even(i % 2 == 0)
                positive(Random.nextInt(200, 1000).toString())
                recover(Random.nextInt(200, 1000).toString())
                death(Random.nextInt(200, 1000).toString())
            }
        }
    }

}