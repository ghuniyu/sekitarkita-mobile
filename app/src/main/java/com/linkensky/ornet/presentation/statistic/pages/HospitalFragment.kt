package com.linkensky.ornet.presentation.statistic.pages

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.existingViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentHospitalBinding
import com.linkensky.ornet.hospital
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.InputText
import com.linkensky.ornet.presentation.base.item.component.LottieLoading
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.statistic.StatisticViewModel
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp

class HospitalFragment : BaseEpoxyFragment<FragmentHospitalBinding>() {

    override var fragmentLayout: Int = R.layout.fragment_hospital

    private val viewModel: StatisticViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = GridLayoutManager(context,1)
        }

        viewModel.getHospitals()
    }

    override fun epoxyController() = buildController(viewModel) { state ->
        addModel(
            "hospital-searcbar", InputText.Model(
                hint = "Rumah Sakit / Kota / Provinsi",
                layout = LayoutOption(margin = Frame(16.dp, 8.dp)),
                itemLayout = LayoutOption(padding = Frame(16.dp, 8.dp)),
                imeOption = EditorInfo.IME_ACTION_DONE,
                drawableStart = R.drawable.ic_google,
                onDoneAction = keyValue(null)
            )
        )

        when(val response = state.hospitals) {
            is Loading -> {
                Log.d("HELLO", "LOADING NIH")
                addModel(
                    "loading-hospitals",
                    LottieLoading()
                )
            }
            is Success -> {
                Log.d("HELLO", "SUCCESS NIH")
                val hospitals = response.invoke()
                hospitals.mapIndexed {i, item ->
                    hospital {
                        id("hospital-$i")
                        name(item.name)
                        address(item.address)
                        phone(item.phone)
                    }
                }
            }
        }
    }
}