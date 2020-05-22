package com.linkensky.ornet.presentation.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.linkensky.ornet.R

abstract class BaseEpoxyFragment<VDB : ViewDataBinding> : BaseMvRxFragment() {
    protected lateinit var binding: VDB
    protected lateinit var recyclerView: EpoxyRecyclerView
    protected lateinit var toolbar: Toolbar
    protected val epoxyController by lazy { epoxyController() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    @LayoutRes
    open var fragmentLayout: Int = R.layout.fragment_base_mvrx

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, fragmentLayout, container, false)
        return binding.root.apply {
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.setController(epoxyController)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
    }

    /**
     * Provide the EpoxyController to use when building models for this Fragment.
     * Basic usages can simply use [simpleController]
     */
    abstract fun epoxyController(): MvRxEpoxyController

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }

    protected fun navigateTo(@IdRes actionId: Int, arg: Parcelable? = null) {
        /**
         * If we put a parcelable arg in [MvRx.KEY_ARG] then MvRx will attempt to call a secondary
         * constructor on any MvRxState objects and pass in this arg directly.
         * @see [com.airbnb.mvrx.sample.features.dadjoke.DadJokeDetailState]
         */
        val bundle = arg?.let { Bundle().apply { putParcelable(MvRx.KEY_ARG, it) } }
        findNavController().navigate(actionId, bundle)
    }
}