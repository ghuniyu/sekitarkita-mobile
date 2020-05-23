package com.linkensky.ornet.presentation.information.pages

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentCallCenterBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class CallCenterFragment : BaseFragment<FragmentCallCenterBinding>() {
    override fun getLayoutRes() = R.layout.fragment_call_center

    private val controller by lazy {
        CallCenterController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = GridLayoutManager(context, 1)
            recyclerView.setController(controller)

            controller.requestModelBuild()
        }
    }

    override fun invalidate() {}
}