package com.linkensky.ornet.presentation.interaction

import com.airbnb.mvrx.withState
import com.linkensky.ornet.itemInteraction
import com.linkensky.ornet.presentation.base.BaseController
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.home.HomeState
import com.linkensky.ornet.presentation.home.HomeViewModel

class InteractionController(private val viewModel: HomeViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) { state ->
        1.rangeTo(100).forEach { i ->
            itemInteraction {
                id("mac-$i")
                text("6e:c7:6d:94:00:c2")
            }
        }
    }
}