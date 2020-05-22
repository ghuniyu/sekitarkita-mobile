package com.linkensky.ornet.presentation.base

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.linkensky.ornet.databinding.FragmentBaseMvrxBinding

abstract class BaseEpoxyBindingFragment : BaseEpoxyFragment<FragmentBaseMvrxBinding>() {

    open val toolbarTitle: String = "SekitarKita"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = LinearLayoutManager(context)
            text = toolbarTitle
        }
    }
}