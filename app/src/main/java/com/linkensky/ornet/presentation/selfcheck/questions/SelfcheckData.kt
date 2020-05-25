package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheckDataBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import kotlinx.android.synthetic.main.fragment_selfcheck_data.*

class SelfcheckData : BaseFragment<FragmentSelfcheckDataBinding>() {

    private val viewModel: SelfcheckViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
        withState(viewModel) { state ->
            super.onViewCreated(view, savedInstanceState)
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                setOnNext {
                    viewModel.nextPage()
                    hideSoftKey(context, view)
                }
                setOnBack {
                    it.findNavController().navigate(R.id.action_selfcheckFragment_to_homeFragment)
                    hideSoftKey(context, view)
                }
            }

            name.setText(state.name)
            phone.setText(state.phone)

            name.doOnTextChanged { text, _, _, _ ->
                viewModel.setName(text.toString())
            }
            phone.doOnTextChanged { text, _, _, _ ->
                viewModel.setPhone(text.toString())
            }
            return@withState
        }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_data
    override fun invalidate() {}
}