package id.ghuniyu.sekitar

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orhanobut.hawk.Hawk
import id.ghuniyu.sekitar.service.ScanService
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {


    private var btAdapter: BluetoothAdapter? = null

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
        setContentView(R.layout.activity_main)
        Hawk.init(this).build()

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null) {
            toast("Perangkat Anda tidak memiliki Bluetooth")
            finish()
            return
        }
        forcePermission()
        bluetoothOn()
    }

    private fun bluetoothOn() {
        if (btAdapter!!.isEnabled) {
            toast("Bluetooth Aktif")
            startService(Intent(this, ScanService::class.java))
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


}
