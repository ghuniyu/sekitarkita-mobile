package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheck4Binding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import kotlinx.android.synthetic.main.fragment_selfcheck_4.*

class Selfcheck4 : BaseFragment<FragmentSelfcheck4Binding>() {

    private val viewModel: SelfcheckViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            page = "4 dari 5"
            lifecycleOwner = viewLifecycleOwner
            setOnNext {
                viewModel.nextPage()
            }
            setOnBack {
                viewModel.prevPage()
            }
            setOnYes { viewModel.inInfectedCity(true) }
            setOnNo { viewModel.inInfectedCity(false) }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_4

    override fun invalidate() = withState(viewModel) {
        if (it.inInfectedCity) {
            yes.setIconResource(R.drawable.ic_check_circle)
            no.setIconResource(0)
        } else {
            yes.setIconResource(0)
            no.setIconResource(R.drawable.ic_check_circle)
        }
    }

}