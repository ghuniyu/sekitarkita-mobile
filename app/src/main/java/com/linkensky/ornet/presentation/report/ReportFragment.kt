package com.linkensky.ornet.presentation.report

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentReportBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyBindingFragment
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.base.MvRxEpoxyController

class ReportFragment : BaseEpoxyBindingFragment() {
    private val controller by lazy {
        ReportController()
    }

    override val toolbarTitle: String = "Lapor Infeksi"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            setOnInfoClick { _ ->
                navigateTo(R.id.action_reportFragment_to_macAddressFragment)
            }
        }
    }

    override fun epoxyController() = controller
}