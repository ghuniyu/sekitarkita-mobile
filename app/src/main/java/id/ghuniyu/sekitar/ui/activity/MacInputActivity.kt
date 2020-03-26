package id.ghuniyu.sekitar.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.utils.Constant
import kotlinx.android.synthetic.main.activity_mac.*
import org.jetbrains.anko.startActivity

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
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.are_you_sure))
                .setMessage(getString(R.string.mac_address_confirm))
                .setPositiveButton(getString(R.string.correct)) { _, _ ->
                    Hawk.put(Constant.STORAGE_MAC_ADDRESS, mac)
                    Toasty.success(this@MacInputActivity, getString(R.string.bluetooth_saved))
                        .show()
                    startActivity<MainActivity>()
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        } else
            Toasty.error(this@MacInputActivity, getString(R.string.incorect_mac_address)).show()
    }
}