package id.ghuniyu.sekitar.ui.activity

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.service.ScanService
import id.ghuniyu.sekitar.utils.Constant
import id.ghuniyu.sekitar.utils.MacAddressRetriever
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.iid.FirebaseInstanceId
import id.ghuniyu.sekitar.service.MessagingService
import id.ghuniyu.sekitar.ui.dialog.LabelDialog
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.stopService


class MainActivity : BaseActivity() {

    private var labelDialog: LabelDialog? = null

    override fun getLayout() = R.layout.activity_main

    companion object {
        private const val EXTRA_ADDRESS = "Device_Address"
        private const val TAG = "MainActivityTag"
        private const val REQUEST_COARSE = 1
        private const val REQUEST_BLUETOOTH = 2
        private const val RESPONSE_BLUETOOTH_DENY = 0
        private const val BLUETOOTH_DENY_MESSAGE = "Aplikasi tidak dapat dijalankan tanpa bluetooth"
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

                "confirmed" -> {
                    status.text = getString(R.string.status, "Confirmed Positif")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Show Terminate on O and Higher */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            destroy.visibility = View.VISIBLE
            destroy.onClick {
                stopService<ScanService>()
            }
        }

        my_label.onClick { showLabelDialog() }
        checkLabel()

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mReceiver, filter)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission Granted
            enableBluetooth()
        } else {
            forceLocationPermission()
        }
        setStatus()

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                task.result?.let {
                    Hawk.put(Constant.STORAGE_FIREBASE_TOKEN, it.token)
                    MessagingService.storeFirebaseToken(this@MainActivity)
                    Log.d(TAG, "getInstanceId success ${it.token}")
                }
            })
    }

    private fun checkLabel() {
        if (!Hawk.get(Constant.STORAGE_ANONYMOUS, false)) {
            if (Hawk.contains(Constant.STORAGE_LABEL)) {
                my_label.text = Hawk.get(Constant.STORAGE_LABEL)
            } else {
                showLabelDialog()
            }
        } else {
            my_label.text = getString(R.string.anonym)
        }
    }

    private fun showLabelDialog() {
        labelDialog = LabelDialog(this)
        labelDialog?.show()
        val save = labelDialog?.find<Button>(R.id.save_label)
        val label = labelDialog?.find<TextInputEditText>(R.id.label_input)
        val neverAsk = labelDialog?.find<Button>(R.id.never_ask)

        save?.onClick {
            label?.let {
                when {
                    it.text.toString().isEmpty() -> {
                        if (Hawk.contains(Constant.STORAGE_LABEL)) {
                            Hawk.delete(Constant.STORAGE_LABEL)
                        }

                        labelDialog?.dismiss()
                        Hawk.put(Constant.STORAGE_ANONYMOUS, true)
                        checkLabel()
                        Log.d(TAG, "StringEmpty Anonymous")

                        Unit
                    }
                    it.text.toString().length > 10 -> {
                        it.error = getString(R.string.max_char)
                    }
                    it.text.toString().length >= 5 -> {
                        Hawk.put(Constant.STORAGE_ANONYMOUS, false)
                        Hawk.put(Constant.STORAGE_LABEL, it.text.toString())
                        Toasty.success(this@MainActivity, getString(R.string.thankyou))
                        labelDialog?.dismiss()
                        checkLabel()
                    }
                    else -> {
                        it.error = getString(R.string.min_char)
                    }
                }
            }
        }

        neverAsk?.onClick {
            Log.d(TAG, "Anonymous")
            Hawk.put(Constant.STORAGE_ANONYMOUS, true)
            if (Hawk.contains(Constant.STORAGE_LABEL)) {
                Hawk.delete(Constant.STORAGE_LABEL)
            }
            labelDialog?.dismiss()
            checkLabel()
        }
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
            Log.d(TAG, getString(R.string.bluetooth_active))
            startService<ScanService>()
        } else {
            retrieveMac()
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val bluetoothState = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (bluetoothState) {
                    BluetoothAdapter.STATE_ON -> {
                        bluetoothOn()
                    }
                    BluetoothAdapter.STATE_OFF -> {
                        enableBluetooth()
                    }
                }
            }
        }
    }

    private fun enableBluetooth() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()

        if (btAdapter === null) {
            Toasty.error(this, getString(R.string.bluetooth_unavailable)).show()
            finish()
            return
        }

        if (!btAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_BLUETOOTH)
        } else {
            bluetoothOn()
        }
    }

    private fun forceLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Should Explain Permission
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.location_permission))
                .setMessage(getString(R.string.request_location_permission))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.understand)) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity as Activity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_COARSE
                    )
                }
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    finish()
                }
                .show()
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
                        enableBluetooth()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH && resultCode == RESPONSE_BLUETOOTH_DENY) {
            Toasty.error(this, BLUETOOTH_DENY_MESSAGE)
                .show()
            finish()
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

    fun selfCheck(view: View) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://ginanjarfm.github.io/covid19diagnose/")
        )
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
        labelDialog?.dismiss()
    }
}
