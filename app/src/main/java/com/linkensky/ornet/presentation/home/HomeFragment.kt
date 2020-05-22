package com.linkensky.ornet.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.linkensky.ornet.*
import com.linkensky.ornet.databinding.FragmentHomeBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.interaction.InteractionController

class HomeFragment : BaseEpoxyFragment<FragmentHomeBinding>() {

    override var fragmentLayout: Int = R.layout.fragment_home

    private val viewModel: HomeViewModel by activityViewModel()

    private val controller by lazy {
        HomeController(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = GridLayoutManager(context, 1)
        }
    }

    override fun epoxyController() = controller

}