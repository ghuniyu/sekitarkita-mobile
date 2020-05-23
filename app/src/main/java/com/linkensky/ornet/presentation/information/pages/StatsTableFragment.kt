package com.linkensky.ornet.presentation.information.pages

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.existingViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentStatsTableBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.information.InformationViewModel

class StatsTableFragment : BaseEpoxyFragment<FragmentStatsTableBinding>() {
    override var fragmentLayout: Int = R.layout.fragment_stats_table
    val viewModel: InformationViewModel by existingViewModel()

    private val controller by lazy {
        StatsTableController(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun epoxyController() = controller
}