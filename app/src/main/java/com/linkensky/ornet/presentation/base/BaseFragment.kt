package com.linkensky.ornet.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.BaseMvRxFragment

abstract class BaseFragment<VDB : ViewDataBinding> : BaseMvRxFragment() {
    protected lateinit var binding: VDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)

        return binding.root
    }

    protected abstract fun getLayoutRes(): Int
}
