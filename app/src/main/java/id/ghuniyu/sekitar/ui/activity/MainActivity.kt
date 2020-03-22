package id.ghuniyu.sekitar.ui.activity

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.service.ScanService
import net.vidageek.mirror.dsl.Mirror
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast

class MainActivity : BaseActivity() {
    private var btAdapter: BluetoothAdapter? = null
    private val FAKE_MAC_ADDRESS = "02:00:00:00:00:00"

    companion object {
        private const val EXTRA_ADDRESS = "Device_Address"
        private const val TAG = "MainActivityTag"
        private const val REQUEST_COARSE = 1
        private const val REQUEST_BLUETOOTH = 2
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_COARSE -> {
                if (resultCode != Activity.RESULT_OK) {
                    forcePermission()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startActivity<InputMacActivity>()
            finish()
        } else {
            btAdapter = BluetoothAdapter.getDefaultAdapter()
            if (btAdapter == null) {
                toast(getString(R.string.bluetooth_unavailable))
                finish()
                return
            }
            forcePermission()
            bluetoothOn()
        }
    }

    override fun getLayout() = R.layout.activity_main

    /**
     * Use this function to get bluetooth mac address from reflection */
    private fun getBluetoothAddress(): String {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var address = bluetoothAdapter.address
        if (address == FAKE_MAC_ADDRESS && Build.VERSION.SDK_INT < 26 /* Oreo */) {
            Log.w(
                TAG,
                "bluetoothAdapter.getAddress() did not return the physical address"
            )

            val bluetoothManagerService: Any =
                Mirror().on(bluetoothAdapter).get().field("mService")
            val internalAddress: Any =
                Mirror().on(bluetoothManagerService).invoke().method("getAddress").withoutArgs()
            if (internalAddress !is String) {
                Log.w(
                    TAG,
                    "Couldn't call bluetoothAdapter.mService.getAddress() using reflection"
                )
                return ""
            }
            address = internalAddress
        }

        if (address == FAKE_MAC_ADDRESS) {
            Log.w(TAG, "Android is actively blocking requests to get the MAC address")
            return ""
        }

        return address
    }

    private fun bluetoothOn() {
        if (btAdapter!!.isEnabled) {
            toast(getString(R.string.bluetooth_active))
            startService<ScanService>()
        } else {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_BLUETOOTH)
        }
    }

    private fun forcePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_COARSE
            )
        }
    }

    fun share(view: View) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        startActivity(shareIntent)
    }
}
