package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.existingViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheck1Binding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel

class Selfcheck1 : BaseEpoxyFragment<FragmentSelfcheck1Binding>() {

    override var fragmentLayout = R.layout.fragment_selfcheck_1

    private val viewModel: SelfcheckViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            setOnNext {
                viewModel.nextPage()
            }
            setOnBack {
                viewModel.prevPage()
            }
        }
    }

    override fun epoxyController() = buildController(viewModel) {

    }

}