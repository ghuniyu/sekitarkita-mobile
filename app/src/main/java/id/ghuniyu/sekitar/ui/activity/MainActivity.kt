package id.ghuniyu.sekitar.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.service.ScanService
import id.ghuniyu.sekitar.utils.Constant
import id.ghuniyu.sekitar.utils.MacAddressRetriever
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService

class MainActivity : BaseActivity() {
    private var btAdapter: BluetoothAdapter? = null

    override fun getLayout() = R.layout.activity_main

    companion object {
        private const val EXTRA_ADDRESS = "Device_Address"
        private const val TAG = "MainActivityTag"
        private const val REQUEST_COARSE = 1
        private const val REQUEST_BLUETOOTH = 2
    }

    private fun setStatus() {
        if (Hawk.contains(Constant.STORAGE_STATUS)) {
            val sts = Hawk.get<String>(Constant.STORAGE_STATUS)
            Log.d(TAG, sts)
            when (sts) {
                "pdp" -> {
                    status.text = getString(R.string.status, "Pasien Dalam Pengawasan")
                    status.setTextColor(ContextCompat.getColor(this, R.color.colorDanger))
                }

                "odp" -> {
                    status.text = getString(R.string.status, "Orang Dalam Pengawasan")
                    status.setTextColor(ContextCompat.getColor(this, R.color.colorWarning))
                }

                else -> {
                    status.text = getString(R.string.status, "Sehat")
                    status.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setStatus()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission Granted
            bluetoothOn()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                var location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    Log.d(TAG, "latitude: ${location.latitude}")
                    Log.d(TAG, "longitude: ${location.longitude}")
                    Hawk.put(Constant.STORAGE_LATEST_LAT, location.latitude)
                    Hawk.put(Constant.STORAGE_LATEST_LNG, location.longitude)
                }
            }
        } else {
            forceLocationPermission()
        }
        setStatus()
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            Log.d(TAG, "latitude: ${mLastLocation.latitude}")
            Log.d(TAG, "longitude: ${mLastLocation.longitude}")
            Hawk.put(Constant.STORAGE_LATEST_LAT, mLastLocation.latitude)
            Hawk.put(Constant.STORAGE_LATEST_LNG, mLastLocation.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun retrieveMac() {
        val mac = MacAddressRetriever.getBluetoothAddress()
        if (mac == "") {
            startActivity<MacInputActivity>()
            finish()
            return
        } else {
            Hawk.put(Constant.STORAGE_MAC_ADDRESS, mac)
            bluetoothOn()
        }
    }

    private fun bluetoothOn() {
        if (Hawk.contains(Constant.STORAGE_MAC_ADDRESS)) {
            btAdapter = BluetoothAdapter.getDefaultAdapter()
            if (btAdapter == null) {
                Toasty.error(this, getString(R.string.bluetooth_unavailable)).show()
                finish()
                return
            }

            if (btAdapter!!.isEnabled) {
                Log.d(TAG, getString(R.string.bluetooth_active))
                startService<ScanService>()
            } else {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_BLUETOOTH)
            }
        } else {
            retrieveMac()
        }
    }

    private fun forceLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Should Explain Permission
            alert {
                message =
                    "Aplikasi SekitarKita membutuhkan Izin Lokasi dan Bluetooth untuk dapat digunakan"
                title = "Izin Lokasi"
                isCancelable = false
                positiveButton("Saya Mengerti") {
                    ActivityCompat.requestPermissions(
                        this@MainActivity as Activity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_COARSE
                    )
                }
                negativeButton("Keluar") {
                    finish()
                }
            }.show()
        } else {
            // No Explanation Needed
            ActivityCompat.requestPermissions(
                (this as Activity?)!!,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_COARSE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_COARSE -> {
                when {
                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        bluetoothOn()
                    }
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) -> {
                        forceLocationPermission()
                    }
                    else -> {
                        Toasty.error(this, "Aplikasi tidak dapat dijalankan tanpa izin Lokasi")
                            .show()
                        finish()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    fun showHistory(view: View) {
        startActivity<InteractionHistoryActivity>()
    }

    fun report(view: View) {
        startActivity<ReportActivity>()
    }
}
