package com.linkensky.ornet.presentation.base.item.component

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.item.ItemModel
import com.linkensky.ornet.presentation.base.item.KeyValue
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.applyLayoutOption
import com.linkensky.ornet.utils.*
import com.minjemin.android.core.item.ModelBind


class InputText(context: Context) : FrameLayout(context), ModelBind<InputText.Model> {
    val item = EditText(context)

    init {
        lparams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(item)
    }


    override fun bind(model: Model) {
        item.apply {
            isEnabled = model.enabled
            setText(model.text.getValue())
            addTextChangedListener(onTextChanged = { text, _, _, _ ->
                model.textChangeListner.getValue()?.invoke(text.toString())
            })
            inputType = model.inputType ?: EditorInfo.TYPE_CLASS_TEXT

            imeOptions = model.imeOption
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    model.onDoneAction.getValue()?.invoke()
                    true
                } else false
            }

            textSize = model.textSize

            background = R.drawable.bg_search_bar.resDrawable();
            hint = model.hint

            if (model.drawableStart != null) {
                setCompoundDrawablesWithIntrinsicBounds(
                    model.drawableStart, 0, 0, 0
                )
                compoundDrawablePadding = 16.dp
            }

            setHintTextColor(R.color.searchBarHint.resColor())
        }

        item.applyLayoutOption(model.itemLayout)
        applyLayoutOption(model.layout)
    }

    data class Model(
        var text: KeyValue<CharSequence?> = KeyValue(null),
        var textChangeListner: KeyValue<((String) -> Unit)?> = KeyValue(null),
        var enabled: Boolean = true,
        var hint: String? = "",
        var textSize: Float = 13f.sp,
        var inputType: Int? = EditorInfo.TYPE_CLASS_TEXT,
        val imeOption: Int = EditorInfo.IME_ACTION_NONE,
        val onDoneAction: KeyValue<(() -> Unit)?> = KeyValue(null),
        val layout: LayoutOption = LayoutOption(),
        val itemLayout: LayoutOption = LayoutOption(),
        val drawableStart: Int? = null
    ) : ItemModel<InputText, Model>(::InputText)
}