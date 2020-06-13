package com.linkensky.ornet.presentation.base.item.component

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.clearWatchers
import com.linkensky.ornet.presentation.base.item.KeyValue
import com.linkensky.ornet.presentation.base.item.LayoutItemModel
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import com.linkensky.ornet.presentation.base.setTextChangedListener
import com.linkensky.ornet.utils.makeTextWatcher
import kotlinx.android.synthetic.main.text_input_item.view.*

data class InputTextMaterial(
    var setText: KeyValue<CharSequence?> = KeyValue(null),
    var textChangeListner: KeyValue<((String) -> Unit)?> = KeyValue(null),
    var enabled: Boolean = true,
    var setHint: String? = "",
    var setInputText: Int? = EditorInfo.TYPE_CLASS_TEXT,
    val imeOption: Int = EditorInfo.IME_ACTION_NONE,
    val onDoneAction: KeyValue<(() -> Unit)?> = KeyValue(null),
    val viewLayout: LayoutOption = LayoutOption(),
    val setError: String = ""
) : LayoutItemModel(R.layout.text_input_item) {
    override fun binder(view: View) = with(view) {
        applyLayoutOption(viewLayout)
        textLayout.apply {
            hint = setHint
            error = setError
        }

        textInput.apply {
            isEnabled = enabled
            setText(setText.getValue())

            setTextChangedListener(makeTextWatcher { text ->
                textChangeListner.getValue()?.invoke(text.toString())
                setSelection(text.length)
            })

            inputType = setInputText ?: EditorInfo.TYPE_CLASS_TEXT
            imeOptions = imeOption
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onDoneAction.getValue()?.invoke()
                    true
                } else false
            }
        }

        Unit
    }

    override fun unbind(view: View) {
        view.textInput.clearWatchers()
        super.unbind(view)
    }
}