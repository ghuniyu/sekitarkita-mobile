package com.linkensky.ornet.presentation.statistic

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentStatisticBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.report.ReportController

class StatisticFragment : BaseFragment<FragmentStatisticBinding>() {
    private val controller by lazy {
        ReportController()
    }

    override fun getLayoutRes() = R.layout.fragment_statistic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.setController(controller)
            recyclerView.layoutManager = GridLayoutManager(context, 1)

            text = "Statistik"
            controller.requestModelBuild()
        }
    }
}