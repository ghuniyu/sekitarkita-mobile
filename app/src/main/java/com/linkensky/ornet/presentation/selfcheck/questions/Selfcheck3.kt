package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheck3Binding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import kotlinx.android.synthetic.main.fragment_selfcheck_3.*

class Selfcheck3 : BaseFragment<FragmentSelfcheck3Binding>() {

    private val viewModel: SelfcheckViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            page = "3 dari 5"
            lifecycleOwner = viewLifecycleOwner
            setOnNext {
                viewModel.nextPage()
            }
            setOnBack {
                viewModel.prevPage()
            }
            setOnYes { viewModel.inInfectedCountry(true) }
            setOnNo { viewModel.inInfectedCountry(false) }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_3

    override fun invalidate() = withState(viewModel) {
        if (it.inInfectedCountry) {
            yes.setIconResource(R.drawable.ic_check_circle)
            no.setIconResource(0)
        } else {
            yes.setIconResource(0)
            no.setIconResource(R.drawable.ic_check_circle)
        }
    }

}