package com.linkensky.ornet.presentation.information.pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.existingViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentCallCenterBinding
import com.linkensky.ornet.itemCallCenter
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.InputText
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.information.InformationViewModel
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp

class CallCenterFragment : BaseEpoxyFragment<FragmentCallCenterBinding>() {

    override var fragmentLayout = R.layout.fragment_call_center

    private val viewModel: InformationViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = GridLayoutManager(context, 1)
        }

        viewModel.getCallCenters()
    }

    override fun epoxyController() = buildController(viewModel) { state ->
        addModel(
            "call-center-search-bar", InputText.Model(
                hint = "Kota / Provinsi",
                layout = LayoutOption(margin = Frame(16.dp, 8.dp)),
                itemLayout = LayoutOption(padding = Frame(16.dp, 8.dp)),
                imeOption = EditorInfo.IME_ACTION_DONE,
                drawableStart = R.drawable.ic_google,
                onDoneAction = keyValue(null)
            )
        )

        when (val response = state.callCenters) {
            is Loading -> {
                addModel(
                    "loading-call-centers",
                    LottieLoading()
                )
            }
            is Success -> {
                val callCenters = response.invoke()
                callCenters.mapIndexed { i, item ->
                    itemCallCenter {
                        id("cs-$i")
                        province(item.area_detail)
                        area(item.area)
                        onCall { _ ->
                            val dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data = Uri.parse("tel:${item.phone}")
                            startActivity(dialIntent)
                        }
                        onBrowse { _ ->
                            val dialIntent = Intent(Intent.ACTION_VIEW)
                            dialIntent.data = Uri.parse(item.website)
                            startActivity(dialIntent)
                        }
                    }
                }
            }
        }
    }
}