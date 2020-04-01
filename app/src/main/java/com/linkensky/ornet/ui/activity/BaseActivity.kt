package com.linkensky.ornet.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.orhanobut.hawk.Hawk

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())

        Hawk.init(this).build()
        AndroidThreeTen.init(this);
    }

    abstract fun getLayout(): Int

    fun Int.resDrawable() = ContextCompat.getDrawable(applicationContext, this)
    fun String.isPhone() = this.length in 10..13 && this.matches(Regex("\\+?([ -]?\\d+)+|\\(\\d+\\)([ -]\\d+)\n"))
}