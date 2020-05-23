package com.linkensky.ornet.presentation.base.item

import android.view.View
import androidx.annotation.LayoutRes
import com.airbnb.epoxy.EpoxyModel

/**
 * Epoxy model using layout res
 */
abstract class LayoutItemModel(
    @LayoutRes private val layoutRes: Int
) : EpoxyModel<View>() {

    private var view: View? = null

    abstract fun binder(view: View)
    override fun bind(view: View) {
        this.view = view
        binder(view)
    }

    override fun unbind(view: View) {
        this.view = null
    }

    override fun getDefaultLayout() = layoutRes
}