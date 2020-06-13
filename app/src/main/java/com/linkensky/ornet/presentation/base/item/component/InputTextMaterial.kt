package com.linkensky.ornet.presentation.base.item.component

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.KeyValue
import com.linkensky.ornet.presentation.base.item.LayoutItemModel
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import kotlinx.android.synthetic.main.text_input_item.view.*

data class InputTextMaterial(
    var setText: KeyValue<CharSequence?> = KeyValue(null),
    var textChangeListner: KeyValue<((String) -> Unit)?> = KeyValue(null),
    var enabled: Boolean = true,
    var setHint: String? = "",
    var setInputText: Int? = EditorInfo.TYPE_CLASS_TEXT,
    val imeOption: Int = EditorInfo.IME_ACTION_NONE,
    val onDoneAction: KeyValue<(() -> Unit)?> = KeyValue(null),
    val inputLayout: ((layout: TextInputLayout) -> Unit)? = null,
    val viewLayout: LayoutOption = LayoutOption()
): LayoutItemModel(R.layout.text_input_item) {
    override fun binder(view: View) = with(view) {
        textInput.apply {
            isEnabled = enabled
            setText(setText.getValue())
            doOnTextChanged { text, _, _, _ ->
                textChangeListner.getValue()?.invoke(text.toString())
            }

            inputType = setInputText ?: EditorInfo.TYPE_CLASS_TEXT

            imeOptions = imeOption
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onDoneAction.getValue()?.invoke()
                    true
                } else false
            }
        }
        inputLayout?.invoke(textLayout)
        textLayout.hint = setHint
        applyLayoutOption(viewLayout)
    }
}