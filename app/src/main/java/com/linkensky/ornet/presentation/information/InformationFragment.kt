package com.linkensky.ornet.presentation.information

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentInformationBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class InformationFragment : BaseFragment<FragmentInformationBinding>() {
    override fun getLayoutRes() = R.layout.fragment_information

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            text = "Informasi Lainnya"

            pager.offscreenPageLimit = 3
            pager.adapter = PagerAdapter(childFragmentManager)
            tab.setupWithViewPager(pager)

            setOnInfoClick { view ->
                view.findNavController().navigate(R.id.action_statisticFragment_to_aboutFragment)
            }
        }
    }

    override fun invalidate() {}
}