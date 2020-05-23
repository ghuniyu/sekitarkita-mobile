package com.linkensky.ornet.presentation.report

import androidx.annotation.ColorRes
import com.linkensky.ornet.presentation.base.MvRxEpoxyController

data class Report(
    @ColorRes
    val bg: Int,
    val value: String,
    val title: String
)

class ReportController : MvRxEpoxyController() {
    override fun buildModels() {}
}