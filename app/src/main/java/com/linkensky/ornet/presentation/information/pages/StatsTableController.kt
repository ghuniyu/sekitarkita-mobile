package com.linkensky.ornet.presentation.information.pages

import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.linkensky.ornet.data.model.Province
import com.linkensky.ornet.itemStats
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.LottieEmptyState
import com.linkensky.ornet.presentation.base.item.component.LottieErrorState
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.information.InformationViewModel
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp

class StatsTableController(private val viewModel: InformationViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) { state ->
        when (val response = state.provinceStatistics) {
            is Loading -> {
                addModel(
                    "loading-provinces",
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
            is Success -> {
                val data = response.invoke()
                if (data.isEmpty()) renderEmptyState() else renderProvinceData(data)
            }
            is Fail -> {
                addModel(
                    "error-provinces",
                    LottieErrorState(clickListener = keyValue { _ ->
                        viewModel.getProvinces()
                    })
                )
            }
        }
        Unit
    }

    private fun EpoxyController.renderEmptyState() {
        addModel(
            "empty-provinces",
            LottieEmptyState(layout = LayoutOption(margin = Frame(8.dp, 40.dp)))
        )
    }

    private fun EpoxyController.renderProvinceData(data: List<Province>) {
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