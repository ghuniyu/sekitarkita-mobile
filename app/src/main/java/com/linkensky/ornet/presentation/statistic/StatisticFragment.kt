package com.linkensky.ornet.presentation.statistic

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentStatisticBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class StatisticFragment : BaseFragment<FragmentStatisticBinding>() {
    override fun getLayoutRes() = R.layout.fragment_statistic

    val viewModel: StatisticViewModel by activityViewModel();

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            pager.offscreenPageLimit = 2
            pager.adapter = PagerAdapter(parentFragmentManager)
            tab.setupWithViewPager(pager)
            text = "Statistik"

            setOnInfoClick { view ->
                view.findNavController().navigate(R.id.action_statisticFragment_to_macAddressFragment)
            }
        }
    }

    override fun invalidate() {}
}