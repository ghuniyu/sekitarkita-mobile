package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheck2Binding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import kotlinx.android.synthetic.main.fragment_selfcheck_2.*

class Selfcheck2 : BaseFragment<FragmentSelfcheck2Binding>() {

    private val viewModel: SelfcheckViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            page = "2 dari 5"
            lifecycleOwner = viewLifecycleOwner
            setOnNext {
                viewModel.nextPage()
            }
            setOnBack {
                viewModel.prevPage()
            }
            setOnCough { viewModel.cough() }
            setOnFlu { viewModel.flu() }
            setOnSoreThroat { viewModel.soreThroat() }
            setOnBD { viewModel.bD() }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_2

    override fun invalidate() = withState(viewModel) {
        if (it.hasCough)
            cough.setIconResource(R.drawable.ic_check_circle)
        else
            cough.setIconResource(0)

        if (it.hasFlu)
            flu.setIconResource(R.drawable.ic_check_circle)
        else
            flu.setIconResource(0)

        if (it.hasBreathProblem)
            breath.setIconResource(R.drawable.ic_check_circle)
        else
            breath.setIconResource(0)

        if (it.hasSoreThroat)
            throat.setIconResource(R.drawable.ic_check_circle)
        else
            throat.setIconResource(0)
    }

}