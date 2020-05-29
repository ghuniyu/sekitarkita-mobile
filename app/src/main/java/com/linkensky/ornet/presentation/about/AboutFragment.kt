package com.linkensky.ornet.presentation.about

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.linkensky.ornet.BuildConfig
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentAboutBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    override fun getLayoutRes() = R.layout.fragment_about

    override fun invalidate() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            version = "${BuildConfig.VERSION_NAME.toUpperCase()} (${BuildConfig.VERSION_CODE})"
            credit.post {
                credit.fullScroll(View.FOCUS_UP)
            }
        }
    }
}