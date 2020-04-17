package com.linkensky.ornet.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.orhanobut.hawk.Hawk
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())

        Hawk.init(this).build()
        AndroidThreeTen.init(this);
    }

    abstract fun getLayout(): Int

    fun Int.resDrawable() = ContextCompat.getDrawable(applicationContext, this)
    fun String.lower() = this.toLowerCase(Locale.getDefault())
    fun String.isPhone() =
        this.length in 10..13 && this.matches(Regex("\\+?([ -]?\\d+)+|\\(\\d+\\)([ -]\\d+)\n"))

    enum class Type {
        PHONE,
        NUMBER
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun EditText.req(
        error: String,
        type: Type? = null,
        typeError: String? = null,
        fulfil: (() -> Unit)
    ): String {
        type?.let {
            when (it.name) {
                Type.PHONE.name -> {
                    if (!this.text.toString().isPhone()) {
                        this.error = typeError
                    }
                }
            }
        }
        if (this.text.isNullOrBlank())
            this.error = error
        else
            fulfil()
        return this.text.toString()
    }
}