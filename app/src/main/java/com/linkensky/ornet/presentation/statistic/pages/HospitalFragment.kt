package com.linkensky.ornet.presentation.statistic.pages

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentHospitalBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class HospitalFragment : BaseFragment<FragmentHospitalBinding>() {
    override fun getLayoutRes() = R.layout.fragment_hospital
    override fun invalidate() {
    }

    private val controller by lazy {
        HospitalController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.setController(controller)
            recyclerView.layoutManager = GridLayoutManager(context, 1)
            controller.requestModelBuild()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        controller.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        controller.cancelPendingModelBuild()
        super.onDestroyView()
    }
}