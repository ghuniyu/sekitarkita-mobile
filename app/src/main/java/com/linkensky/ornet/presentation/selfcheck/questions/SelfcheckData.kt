package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheckDataBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.selfcheck.SelfcheckState
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import com.linkensky.ornet.utils.validPhone
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_selfcheck_data.*

class SelfcheckData : BaseFragment<FragmentSelfcheckDataBinding>() {

    private val viewModel: SelfcheckViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        withState(viewModel) { state ->
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
                observeName(text)
                viewModel.setName(text.toString())
            }
            phone.doOnTextChanged { text, _, _, _ ->
                observePhoneNumber(text)
                viewModel.setPhone(text.toString())
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_data

    override fun invalidate() {
        withState(viewModel) {state ->
            binding.apply {
                isEnableBtn = observeForm(state)
            }
        }
    }

    private fun observeForm(state: SelfcheckState): Boolean {
        return (!state.name.isNullOrEmpty() && !state.phone.isNullOrEmpty())
                && (!name_layout.isErrorEnabled && !phone_layout.isErrorEnabled)
    }

    private fun observePhoneNumber(text: CharSequence?) {
        if (!text.isNullOrEmpty()) {
            if (!text.toString().validPhone())
                phone_layout.error = INVALID_PHONE
            else
                phone_layout.isErrorEnabled = false
        } else phone_layout.error = PHONE_REQUIRED
    }


    private fun observeName(text: CharSequence?) {
        if (!text.isNullOrEmpty()) name_layout.isErrorEnabled = false
        else name_layout.error = NAME_REQUIRED
    }

    companion object {
        const val NAME_REQUIRED = "Nama wajib diisi"
        const val PHONE_REQUIRED = "Nomer Telepon wajib diisi"
        const val INVALID_PHONE = "Nomer Telepon Tidak Benar"
    }
}