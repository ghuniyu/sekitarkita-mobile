package com.linkensky.ornet.presentation.interaction

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.existingViewModel
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.BaseEpoxyBindingFragment
import com.linkensky.ornet.presentation.home.HomeViewModel
import com.orhanobut.hawk.Hawk

class InteractionFragment : BaseEpoxyBindingFragment() {

    private val viewModel: HomeViewModel by existingViewModel()

    override val toolbarTitle: String = "Interaksi"

    private val controller by lazy {
        InteractionController(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Hawk.contains(Const.DEVICE_ID))
            viewModel.getDeviceInteractionHistory(Hawk.get(Const.DEVICE_ID))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setOnInfoClick { _ ->
                navigateTo(R.id.action_interactionFragment_to_aboutFragment)
            }
        }
    }

    override fun epoxyController() = controller
}