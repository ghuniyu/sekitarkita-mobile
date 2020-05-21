package com.linkensky.ornet.presentation.home

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentHomeBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_interaction.*

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun getLayoutRes() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            setOnCallCenterClick {

            }

            setOnHospitalClick {

            }

            setOnSelfTestClick {

            }
        }
    }

}