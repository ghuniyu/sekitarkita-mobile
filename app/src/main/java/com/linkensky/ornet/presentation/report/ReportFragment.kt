package com.linkensky.ornet.presentation.report

import android.os.Bundle
import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentReportBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class ReportFragment : BaseFragment<FragmentReportBinding>() {
    private val controller by lazy {
        ReportController()
    }

    override fun getLayoutRes() = R.layout.fragment_report
    override fun invalidate() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            text = "Lapor Infeksi"
            controller.requestModelBuild()
        }
    }
}