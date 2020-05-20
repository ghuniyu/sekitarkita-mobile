package com.linkensky.ornet.presentation.home

import android.os.Bundle
import android.view.View
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentHomeBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val controller by lazy {
        HomeController()
    }

    override fun getLayoutRes() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

}