package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheck1Binding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import kotlinx.android.synthetic.main.fragment_selfcheck_1.*

class Selfcheck1 : BaseFragment<FragmentSelfcheck1Binding>() {

    private val viewModel: SelfcheckViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            page = "1 dari 5"
            lifecycleOwner = viewLifecycleOwner
            setOnNext { viewModel.nextPage() }
            setOnBack {
                it.findNavController().navigate(R.id.action_selfcheckFragment_to_homeFragment)
            }

            setOnYes { viewModel.hasFever(true) }
            setOnNo { viewModel.hasFever(false) }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_1

    override fun invalidate() = withState(viewModel) {
        if (it.hasFever) {
            yes.setIconResource(R.drawable.ic_check_circle)
            no.setIconResource(0)
        } else {
            yes.setIconResource(0)
            no.setIconResource(R.drawable.ic_check_circle)
        }
    }
}