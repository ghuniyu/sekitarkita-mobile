package com.linkensky.ornet.presentation.interaction

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.existingViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.BaseEpoxyBindingFragment
import com.linkensky.ornet.presentation.home.HomeViewModel

class InteractionFragment : BaseEpoxyBindingFragment() {

    private val viewModel: HomeViewModel by existingViewModel()

    override val toolbarTitle: String = "Interaksi"

    private val controller by lazy {
        InteractionController(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setOnInfoClick { _ ->
                navigateTo(R.id.action_interactionFragment_to_macAddressFragment)
            }
        }
    }

    override fun epoxyController() = controller
}