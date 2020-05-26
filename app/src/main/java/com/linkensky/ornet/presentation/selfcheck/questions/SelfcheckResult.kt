package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheckResultBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel

class SelfcheckResult : BaseFragment<FragmentSelfcheckResultBinding>() {

    private val viewModel: SelfcheckViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            setOnClose {
                view.findNavController().popBackStack(R.id.homeFragment, false)
                viewModel.clearAllState()
            }
            setOnRetake { viewModel.firstPage() }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_result

    override fun invalidate() = withState(viewModel) {
        binding.apply {
            result = it.status.toString()
            illustration = it.status.getDrawable()
        }
        return@withState
    }
}