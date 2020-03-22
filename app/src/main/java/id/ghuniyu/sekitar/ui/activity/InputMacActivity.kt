package id.ghuniyu.sekitar.ui.activity

import android.content.Intent
import android.view.View
import id.ghuniyu.sekitar.R

class InputMacActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_mac

    fun openDeviceInfo(view: View) {
        startActivity(Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS))
    }
}