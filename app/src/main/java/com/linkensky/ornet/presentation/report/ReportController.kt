package com.linkensky.ornet.presentation.report

import androidx.annotation.ColorRes
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.BaseController
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.report

data class Report(
    @ColorRes
    val bg: Int,
    val value: String,
    val title: String
)

class ReportController : MvRxEpoxyController() {
    override fun buildModels() {
        val reports = arrayOf(
            Report(R.color.colorDanger, "odp", "Saya adalah ODP"),
            Report(R.color.colorAccent, "pdp", "Saya adalah PDP"),
            Report(R.color.colorPrimary, "positive", "Saya adalah POSITIF"),
            Report(R.color.blue, "healthy", "Saya adalah SEHAT")
        )

        reports.map {
            report {
                id(it.value)
                cardBg(it.bg)
                title(it.title)
            }
        }
    }
}