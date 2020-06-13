package com.linkensky.ornet.presentation.base

import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.airbnb.epoxy.EpoxyController
import com.linkensky.ornet.R


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

@BindingAdapter("inputText")
fun setInputText(editText: EditText, text: CharSequence?) {
    if (setText(editText, text)) {
        // We already know the text differs, but find the last edit location and use
        // that as the selection.
        editText.setSelection(editText.length())
    }
}

private fun setText(textView: TextView, text: CharSequence?): Boolean {
    if (!isTextDifferent(text, textView.text)) {
        // Previous text is the same. No op
        return false
    }
    textView.text = text
    return true
}

private fun isTextDifferent(str1: CharSequence?, str2: CharSequence?): Boolean =
    when {
        str1 === str2 -> false
        str1 == null || str2 == null -> true
        str1.length != str2.length -> true
        else ->
            str1.toString() != str2.toString() // Needed in case either string is a Spannable
    }

@BindingAdapter("textWatcher")
fun setTextWatcher(textView: TextView, textWatcher: TextWatcher?) {
    textView.setTextChangedListener(textWatcher)
}

fun TextView.setTextChangedListener(watcher: TextWatcher?) {
    clearWatchers()
    watcher?.let {
        addTextChangedListener(it)
        getWatchers().add(it)
    }
}

fun TextView.clearWatchers() {
    val watchers = getWatchers()
    watchers.forEach {
        removeTextChangedListener(it)
    }
    watchers.clear()
}

private fun TextView.getWatchers(): MutableList<TextWatcher> {
    @Suppress("UNCHECKED_CAST")
    return getTag(R.id.text_watchers) as? MutableList<TextWatcher> ?: run {
        val newList = mutableListOf<TextWatcher>()
        setTag(R.id.text_watchers, newList)
        newList
    }
}

abstract class BaseController : EpoxyController()