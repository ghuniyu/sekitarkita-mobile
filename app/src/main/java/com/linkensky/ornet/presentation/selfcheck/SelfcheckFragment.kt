package com.linkensky.ornet.presentation.selfcheck

import android.os.Bundle
import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheck1Binding
import com.linkensky.ornet.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_selfcheck.*

class SelfcheckFragment : BaseFragment<FragmentSelfcheck1Binding>() {
    override fun getLayoutRes() = R.layout.fragment_selfcheck

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            pager.offscreenPageLimit = 5
            pager.adapter = SelfcheckPagerAdapter(parentFragmentManager)
        }
    }

    override fun invalidate() {}
}