package com.linkensky.ornet.presentation.statistic.pages

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentStatsTableBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class StatsTableFragment : BaseFragment<FragmentStatsTableBinding>() {
    override fun getLayoutRes() = R.layout.fragment_stats_table

    private val controller by lazy {
        StatsTableController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.setController(controller)
            recyclerView.layoutManager = GridLayoutManager(context, 1)
            controller.requestModelBuild()
        }
    }
}