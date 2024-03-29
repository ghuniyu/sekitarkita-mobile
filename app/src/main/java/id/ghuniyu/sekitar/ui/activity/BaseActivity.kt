package id.ghuniyu.sekitar.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.orhanobut.hawk.Hawk

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())

        Hawk.init(this).build()
    }

    abstract fun getLayout(): Int

    fun Int.resDrawable() = ContextCompat.getDrawable(applicationContext, this)
    fun String.isPhone() = this.length in 10..13 && this.matches(Regex("\\+?([ -]?\\d+)+|\\(\\d+\\)([ -]\\d+)\n"))
}