package com.linkensky.ornet.presentation.base.item

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyModel

/**
 * Epoxy Model for custom view
 */
abstract class ItemModel<T, S>(val viewConstructor: (Context) -> T):
    EpoxyModel<T>()  where T : View, T: ModelBind<S> {

    private var view: View? = null

    override fun buildView(parent: ViewGroup): View = viewConstructor(parent.context)
    override fun getDefaultLayout(): Int = 0
    override fun bind(view: T) {
        this.view = view
        @Suppress("UNCHECKED_CAST")
        view.bind(this as S)
    }

    override fun unbind(view: T) {
        this.view = null
    }
}