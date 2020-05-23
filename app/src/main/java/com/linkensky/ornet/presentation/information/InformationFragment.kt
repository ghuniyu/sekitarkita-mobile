package com.linkensky.ornet.presentation.information

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.airbnb.mvrx.activityViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentStatisticBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class InformationFragment : BaseFragment<FragmentStatisticBinding>() {
    override fun getLayoutRes() = R.layout.fragment_statistic

    val viewModel: InformationViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            pager.offscreenPageLimit = 2
            pager.adapter = PagerAdapter(parentFragmentManager)
            tab.setupWithViewPager(pager)
            text = "Informasi"

            setOnInfoClick { view ->
                view.findNavController().navigate(R.id.action_statisticFragment_to_macAddressFragment)
            }
        }
    }

    override fun invalidate() {}
}