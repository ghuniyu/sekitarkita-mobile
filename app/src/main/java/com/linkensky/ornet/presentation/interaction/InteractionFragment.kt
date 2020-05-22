package com.linkensky.ornet.presentation.interaction

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentInteractionBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class InteractionFragment : BaseFragment<FragmentInteractionBinding>() {

    private val controller by lazy {
        InteractionController()
    }

    override fun getLayoutRes() = R.layout.fragment_interaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.setController(controller)
            recyclerView.layoutManager = GridLayoutManager(context, 1)

            text = "Interaction"
            setOnInfoClick { view ->
                view.findNavController().navigate(R.id.action_interactionFragment_to_macAddressFragment)
            }
            controller.requestModelBuild()
        }
    }
}