package id.ghuniyu.sekitar.ui.activity

import android.content.Intent
import android.nfc.Tag
import android.util.Log
import android.view.View
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.utils.Constant
import kotlinx.android.synthetic.main.activity_mac.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton

class MacInputActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_mac

    companion object {
        const val TAG = "MacInputActivityTag"
    }

    fun openDeviceInfo(view: View) {
        startActivity(Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS))
    }

    fun save(view: View) {
        val mac = mac_field.text.toString()
        Log.d(TAG, mac)
        if (mac.length == 17) {
            alert(
                "Apakah Bluetooth Address ini Benar?",
                getString(R.string.are_you_sure)
            ) {
                yesButton {
                    Hawk.put(Constant.STORAGE_MAC_ADDRESS, mac)
                    Toasty.success(this@MacInputActivity, "Bluetooth Address Tersimpan").show()
                    startActivity<MainActivity>()
                    finish()
                }
                noButton { }
            }.show()
        } else
            Toasty.error(this@MacInputActivity, "Bluetooth Address Tidak Sesuai").show()
    }
}