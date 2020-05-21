package com.linkensky.ornet.presentation.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentHomeBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_interaction.*

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val controller by lazy {
        HomeController()
    }

    override fun getLayoutRes() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.setController(controller)
            recyclerView.layoutManager = GridLayoutManager(context, 1)

            controller.requestModelBuild()
        }
    }

}