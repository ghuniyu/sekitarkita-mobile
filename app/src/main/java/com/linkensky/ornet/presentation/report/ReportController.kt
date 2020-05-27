package com.linkensky.ornet.presentation.report

import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.withState
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp

class ReportController(private val viewModel: ReportViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) { state ->
        when (state.responsePostChangeStatus) {
            is Loading -> addModel(
                "loading-device-history",
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
        }
    }
}