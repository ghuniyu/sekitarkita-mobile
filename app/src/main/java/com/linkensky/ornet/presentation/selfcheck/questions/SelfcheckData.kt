package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheckDataBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckState
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import com.linkensky.ornet.utils.validPhone
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
                    it.findNavController().popBackStack()
                    hideSoftKey(context, view)
                }
            }

            name.setText(state.name)
            phone.setText(state.phone)
            age.setText(state.age)
            address.setText(state.address)

            name.doOnTextChanged { text, _, _, _ ->
                observeName(text)
                viewModel.setName(text.toString())
            }
            phone.doOnTextChanged { text, _, _, _ ->
                observePhoneNumber(text)
                viewModel.setPhone(text.toString())
            }

            age.doOnTextChanged { text, _, _, _ ->
                observeAge(text)
                viewModel.setAge(text.toString())
            }

            address.doOnTextChanged { text, _, _, _ ->
                observeAddress(text)
                viewModel.setAddress(text.toString())
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
        return when {
            state.name.isNullOrEmpty() -> false
            state.phone.isNullOrEmpty() -> false
            state.age.isNullOrEmpty() -> false
            state.address.isNullOrEmpty() -> false
            name_layout.isErrorEnabled -> false
            phone_layout.isErrorEnabled -> false
            age_layout.isErrorEnabled -> false
            address_layout.isErrorEnabled -> false
            else -> true
        }
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

    private fun observeAge(text: CharSequence?) {
        if (!text.isNullOrEmpty()) age_layout.isErrorEnabled = false
        else age_layout.error = AGE_REQUIRED
    }

    private fun observeAddress(text: CharSequence?) {
        if (!text.isNullOrEmpty()) address_layout.isErrorEnabled = false
        else address_layout.error = ADDRESS_REQUIRED
    }

    companion object {
        const val NAME_REQUIRED = "Nama wajib diisi"
        const val AGE_REQUIRED = "Umur wajib diisi"
        const val ADDRESS_REQUIRED = "Alamat wajib diisi"
        const val PHONE_REQUIRED = "Nomer Telepon wajib diisi"
        const val INVALID_PHONE = "Nomer Telepon Tidak Benar"
    }
}