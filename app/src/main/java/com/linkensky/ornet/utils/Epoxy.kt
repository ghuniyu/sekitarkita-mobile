package com.linkensky.ornet.utils
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel

fun EpoxyController.addModel(id: String, model: EpoxyModel<*>) {
    model.id(id).addTo(this)
}

fun EpoxyController.addModel(id: Int, model: EpoxyModel<*>) {
    model.id(id).addTo(this)
}

fun EpoxyController.addModel(id: String, model: (Int) -> EpoxyModel<*>) {
    model(id.hashCode()).id(id).addTo(this)
}