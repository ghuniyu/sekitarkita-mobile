package com.linkensky.ornet.presentation.statistic.pages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentStatsTableBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.statistic.StatisticViewModel

class StatsTableFragment : BaseEpoxyFragment<FragmentStatsTableBinding>() {
    override var fragmentLayout: Int = R.layout.fragment_stats_table
    val viewModel: StatisticViewModel by existingViewModel()

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