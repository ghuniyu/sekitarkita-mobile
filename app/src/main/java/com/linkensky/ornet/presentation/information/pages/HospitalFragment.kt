package com.linkensky.ornet.presentation.information.pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.existingViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.Hospital
import com.linkensky.ornet.databinding.FragmentHospitalBinding
import com.linkensky.ornet.hospital
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.InputText
import com.linkensky.ornet.presentation.base.item.component.LottieEmptyState
import com.linkensky.ornet.presentation.base.item.component.LottieErrorState
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.information.InformationViewModel
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp

class HospitalFragment : BaseEpoxyFragment<FragmentHospitalBinding>() {

    override var fragmentLayout: Int = R.layout.fragment_hospital

    private val viewModel: InformationViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = GridLayoutManager(context, 1)
        }

        viewModel.getHospitals()
    }

    override fun epoxyController() = buildController(viewModel) { state ->
        addModel(
            "hospital-search-bar", InputText.Model(
                hint = "Rumah Sakit / Kota / Provinsi",
                layout = LayoutOption(margin = Frame(16.dp, 8.dp)),
                itemLayout = LayoutOption(padding = Frame(16.dp, 8.dp)),
                imeOption = EditorInfo.IME_ACTION_DONE,
                drawableStart = R.drawable.ic_google,
                onDoneAction = keyValue(null)
            )
        )

        when (val response = state.hospitals) {
            is Loading -> {
                addModel(
                    "loading-hospitals",
                    LottieLoading()
                )
            }
            is Success -> {
                val hospitals = response.invoke()
                if (hospitals.isEmpty()) renderEmptyState() else renderHospitals(hospitals)
            }
            is Fail -> {
                addModel(
                    "error-hospitals",
                    LottieErrorState(clickListener = keyValue { _ ->
                        viewModel.getHospitals()
                    })
                )
            }
        }
    }

    private fun EpoxyController.renderEmptyState() {
        addModel(
            "empty-hospitals",
            LottieEmptyState(layout = LayoutOption(margin = Frame(8.dp, 40.dp)))
        )
    }

    private fun EpoxyController.renderHospitals(hospitals: List<Hospital>) {
        hospitals.mapIndexed { i, item ->
            hospital {
                id("hospital-$i")
                name(item.name)
                address(item.address)
                phone(item.phone)
                onCall { _ ->
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:${item.phone}")
                    startActivity(dialIntent)
                }
                onShowMap { _ ->
                    val location = "geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}"
                    val showMapIntent = Intent(Intent.ACTION_VIEW)
                    showMapIntent.setPackage("com.google.android.apps.maps")
                    showMapIntent.data = Uri.parse(location)
                    startActivity(showMapIntent)
                }
            }
        }
    }
}