package com.linkensky.ornet.presentation.base

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.airbnb.epoxy.EpoxyController


@BindingAdapter("android:layout_marginStart")
fun setMarginStart(view: View, value: Int) {
    val lp = view.layoutParams as ViewGroup.MarginLayoutParams
    lp.setMargins(value, lp.topMargin, lp.rightMargin, lp.bottomMargin)
    view.layoutParams = lp
}

@BindingAdapter("android:layout_marginEnd")
fun setMarginEnd(view: View, value: Int) {
    val lp = view.layoutParams as ViewGroup.MarginLayoutParams
    lp.setMargins(lp.leftMargin, lp.topMargin, value, lp.bottomMargin)
    view.layoutParams = lp
}

@BindingAdapter("android:src")
fun setImageUri(view: ImageView, imageUri: String?) {
    if (imageUri == null) {
        view.setImageURI(null)
    } else {
        view.setImageURI(Uri.parse(imageUri))
    }
}

@BindingAdapter("android:src")
fun setImageUri(view: ImageView, imageUri: Uri?) {
    view.setImageURI(imageUri)
}

@BindingAdapter("android:src")
fun setImageDrawable(view: ImageView, drawable: Drawable?) {
    view.setImageDrawable(drawable)
}

@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("app:cardBackgroundColor")
fun setCardBg(cardView: CardView, resource: Int){
    cardView.setCardBackgroundColor(resource)
}

@BindingAdapter("android:focusable")
fun setFocusable(editText: EditText, isFocus: Boolean) {
    editText.isFocusable = isFocus;
}

abstract class BaseController : EpoxyController()