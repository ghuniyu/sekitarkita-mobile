package com.linkensky.ornet.presentation.interaction

import androidx.navigation.findNavController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.Interaction
import com.linkensky.ornet.itemInteraction
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.LottieEmptyState
import com.linkensky.ornet.presentation.base.item.component.LottieErrorState
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.home.HomeViewModel
import com.linkensky.ornet.utils.Formatter
import com.linkensky.ornet.utils.Formatter.Companion.statusify
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp
import com.orhanobut.hawk.Hawk

class InteractionController(private val viewModel: HomeViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) { state ->
        if (Hawk.contains(Const.DEVICE_ID)) {
            when (val response = state.historyInteraction) {
                is Loading -> {
                    addModel(
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
                is Success -> {
                    val data = response.invoke().nearbies
                    if (data.isEmpty()) renderEmptyState() else renderDeviceHistory(data)
                }
                is Fail -> {
                    addModel(
                        "error-device-history",
                        LottieErrorState(clickListener = keyValue { _ ->
                            viewModel.getDeviceInteractionHistory(Hawk.get(Const.DEVICE_ID))
                        })
                    )
                }
            }
        } else {
            renderEmptyState()
        }
    }

    private fun EpoxyController.renderEmptyState() {
        addModel(
            "empty-device-history",
            LottieEmptyState(layout = LayoutOption(margin = Frame(8.dp, 40.dp)))
        )
    }

    private fun EpoxyController.renderDeviceHistory(data: List<Interaction>) {
        data.mapIndexed { i, it ->
            itemInteraction {
                id("device-history-$i")
                text(it.another_device)
                textStatus(it.user_status.statusify())
                textDate(
                    Formatter.dateFormat(
                        it.created_at,
                        Formatter.Template.STRING_WEEK_OF_DAY_DAY_MONTH_YEAR
                    )
                )
                textTime(Formatter.dateFormat(it.created_at, Formatter.Template.TIME))
                onClickItem { v ->
                    viewModel.setInteraction(it)
                    v.findNavController()
                        .navigate(R.id.action_interactionFragment_to_interactionDetailFragment)
                }
            }
        }
    }
}