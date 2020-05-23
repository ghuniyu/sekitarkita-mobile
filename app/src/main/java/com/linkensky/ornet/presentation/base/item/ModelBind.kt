package com.minjemin.android.core.item

/**
 * Used as a pair for ItemModel
 * bind() method will be called to render view based on model
 * @see com.btech.spectrum.view.general.ButtonItemView for example
 */
interface ModelBind<S> {
    fun bind(model: S)
}