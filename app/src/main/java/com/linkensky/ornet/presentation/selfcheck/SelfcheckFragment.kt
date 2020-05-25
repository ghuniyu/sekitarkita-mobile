package com.linkensky.ornet.presentation.selfcheck

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheckBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_selfcheck.*

class SelfcheckFragment : BaseFragment<FragmentSelfcheckBinding>() {
    override fun getLayoutRes() = R.layout.fragment_selfcheck

    val viewModel: SelfcheckViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val adapter = SelfcheckPagerAdapter(parentFragmentManager, lifecycle)
            lifecycleOwner = viewLifecycleOwner
            pager.offscreenPageLimit = adapter.itemCount
            pager.adapter = adapter
            pager.isUserInputEnabled = false
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        pager.currentItem = state.page - 1
    }
}