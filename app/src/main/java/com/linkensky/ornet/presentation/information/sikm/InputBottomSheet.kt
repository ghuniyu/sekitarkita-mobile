package com.linkensky.ornet.presentation.information.sikm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.BuildConfig
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.WebviewActivity
import com.linkensky.ornet.presentation.base.BaseEpoxySheetFragment
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.InputTextMaterial
import com.linkensky.ornet.presentation.base.item.component.MaterialButtonView
import com.linkensky.ornet.presentation.base.item.component.ViewText
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.base.item.layout
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp
import com.linkensky.ornet.utils.sp
import kotlinx.android.synthetic.main.bottom_sheet_with_recycler.*

class InputBottomSheet : BaseEpoxySheetFragment() {

    private val viewModel: InputBottomSheetViewModel by fragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_title.text = "Cek SIKM"
    }

    override fun epoxyController() = buildController(viewModel) { state ->
        addModel(
            "labelInfo", ViewText.Model(
                text = "Untuk mengecek SIKM masukan NIK (Nomer Induk Penduduk) yang sudah didaftarkan",
                textSize = 14f.sp,
                layout = layout {
                    margin = Frame(
                        top = 8.dp,
                        left = 16.dp,
                        right = 16.dp,
                        bottom = 8.dp
                    )
                }
            )
        )
        addModel(
            "noKtpModel", InputTextMaterial(
                setHint = "Masukan NIK",
                setInputText = EditorInfo.TYPE_CLASS_NUMBER,
                setText = keyValue(state.nik),
                imeOption = EditorInfo.IME_ACTION_SEARCH,
                textChangeListner = keyValue { string ->
                    if (string.length != 16) {
                        viewModel.setErrorNik("NIK Harus 16 Digit")
                    } else {
                        viewModel.setErrorNik("")
                    }
                    viewModel.setNik(string)
                },
                onDoneAction = keyValue {
                    if (state.nikError.isEmpty()) {
                        doCheck()
                        hideSoftKey(requireContext(), requireView())
                    }
                },
                setError = state.nikError,
                viewLayout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 16.dp))
            )
        )
    }

    private fun doCheck() = withState(viewModel) {
        val intent = Intent(requireContext(), WebviewActivity::class.java)
        intent.putExtra("url", "${BuildConfig.APP_WEB_URL}sikm/${it.nik}")
        startActivityForResult(intent, 50)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialog?.dismiss()
    }
}

data class InputBottomSheetState(
    val nik: String = "",
    val nikError: String = ""
) : MvRxState

class InputBottomSheetViewModel(state: InputBottomSheetState) :
    MvRxViewModel<InputBottomSheetState>(state) {
    fun setNik(value: String) = setState { copy(nik = value) }
    fun setErrorNik(value: String) = setState { copy(nikError = value) }
}