package id.ghuniyu.sekitar.ui.component

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText


class MacInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : EditText(context, attrs, defStyleAttr) {

    var mPreviousMac: String? = null

    init {
        this.addTextChangedListener(object : TextWatcher {
            private fun setMacEdit(
                cleanMac: String,
                formattedMac: String,
                selectionStart: Int,
                lengthDiff: Int
            ) {
                this@MacInput.removeTextChangedListener(this)
                if (cleanMac.length <= 12) {
                    this@MacInput.setText(formattedMac)
                    this@MacInput.setSelection(selectionStart + lengthDiff)
                    mPreviousMac = formattedMac
                } else {
                    this@MacInput.setText(mPreviousMac)
                    mPreviousMac?.length?.let {
                        this@MacInput.setSelection(it)
                    }
                }
                this@MacInput.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val enteredMac: String =
                    this@MacInput.text.toString().toUpperCase()
                val cleanMac: String = clearNonMacCharacters(enteredMac)
                var formattedMac: String = formatMacAddress(cleanMac)
                val selectionStart: Int = this@MacInput.selectionStart
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart)
                val lengthDiff = formattedMac.length - enteredMac.length
                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun handleColonDeletion(
        enteredMac: String,
        input: String,
        selectionStart: Int
    ): String {
        var formattedMac = input
        if (mPreviousMac != null && mPreviousMac!!.length > 1) {
            val previousColonCount = colonCount(mPreviousMac!!)
            val currentColonCount = colonCount(enteredMac)
            if (currentColonCount < previousColonCount) {
                formattedMac =
                    formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(
                        selectionStart
                    )
                val cleanMac = clearNonMacCharacters(formattedMac)
                formattedMac = formatMacAddress(cleanMac)
            }
        }
        return formattedMac
    }

    private fun formatMacAddress(cleanMac: String): String {
        var groupedCharacters = 0
        var formattedMac = ""
        for (element in cleanMac) {
            formattedMac += element
            ++groupedCharacters
            if (groupedCharacters == 2) {
                formattedMac += ":"
                groupedCharacters = 0
            }
        }
        if (cleanMac.length == 12) {
            formattedMac = formattedMac.substring(0, formattedMac.length - 1)
        }
        return formattedMac
    }

    private fun clearNonMacCharacters(mac: String): String {
        return mac.replace("[^A-Fa-f0-9]".toRegex(), "")
    }

    private fun colonCount(formattedMac: String): Int {
        return formattedMac.replace("[^:]".toRegex(), "").length
    }

}