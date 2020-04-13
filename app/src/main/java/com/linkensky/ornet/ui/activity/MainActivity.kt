package com.linkensky.ornet.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.linkensky.ornet.BuildConfig
import com.linkensky.ornet.R
import com.linkensky.ornet.service.*
import com.linkensky.ornet.data.callback.CollectionCallback
import com.linkensky.ornet.data.model.Partner
import com.linkensky.ornet.data.remote.Client
import com.linkensky.ornet.data.response.BaseCollectionResponse
import com.linkensky.ornet.service.MessagingService
import com.linkensky.ornet.service.ScanService
import com.linkensky.ornet.ui.dialog.LabelDialog
import com.linkensky.ornet.utils.CheckAutostartPermission
import com.linkensky.ornet.utils.Constant
import com.linkensky.ornet.utils.Formatter
import com.linkensky.ornet.utils.MacAddressRetriever
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import org.jetbrains.anko.stopService
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import retrofit2.Response
import kotlin.system.exitProcess


class MainActivity : BaseActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var labelDialog: LabelDialog? = null
    private val autoStart = CheckAutostartPermission.getInstance()
    private val redacted = arrayOf(67, 111, 118, 105, 100, 45, 49, 57)
    private val selfCheck =
        arrayOf(99, 111, 118, 105, 100, 49, 57, 100, 105, 97, 103, 110, 111, 115, 101)

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun getLayout() = R.layout.activity_main

    companion object {
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
                    status.setTextColor(ContextCompat.getColor(this, R.color.warning))
                }

                "odp" -> {
                    status.text = getString(R.string.status, "Orang Dalam Pengawasan")
                    status.setTextColor(ContextCompat.getColor(this, R.color.yellow))
                }

                "confirmed" -> {
                    status.text = getString(R.string.status, "Confirmed Positif")
                    status.setTextColor(ContextCompat.getColor(this, R.color.danger))
                }

                else -> {
                    status.text = getString(R.string.status, "Sehat")
                    status.setTextColor(ContextCompat.getColor(this, R.color.success))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkArea()
        setStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        destroy.onClick {
            stopService<ScanService>()
            stopService<LocationService>()
            stopService<PingService>()
            finish()
            exitProcess(0)
        }

        version.text = getString(
            R.string.version,
            BuildConfig.VERSION_NAME.toUpperCase(),
            BuildConfig.VERSION_CODE
        )

        my_label.onClick { showLabelDialog() }
        checkLabel()

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mReceiver, filter)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission Granted
            checkArea()
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

        checkRemoteConfig()
        scheduleScore()
    }

    private fun getPartners() {
        Client.service.getPartners()
            .enqueue(object : CollectionCallback<List<Partner>>(this) {
            override fun onSuccess(response: Response<BaseCollectionResponse<List<Partner>>>) {
                super.onSuccess(response)
                response.body()?.data?.let { list ->
                    list.forEach {
                        partners_slider.addSlider(
                            TextSliderView(this@MainActivity)
                                .description(it.name)
                                .image("${BuildConfig.APP_IMAGE_URL}${it.logo}")
                                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        )
                    }
                    partners_slider.setDuration(5000)
                }
            }
        })
    }

    private fun scheduleScore() {
        val notifyIntent = Intent(this, NotificationPublisher::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val date = LocalDateTime.now()
        val eight = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 48))

        if (date.isAfter(eight)) {
            eight.plusDays(1)
        }
        val epoch = eight.toEpochSecond(ZoneOffset.ofHours(7))
        Log.d(TAG, "Notification at $epoch")
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            epoch,
            pendingIntent
        )
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
            checkAutostart()
            Log.d(TAG, getString(R.string.bluetooth_active))
            startService<ScanService>()
            getPartners()
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
                        checkArea()
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
            putExtra(
                Intent.EXTRA_TEXT,
                getString(
                    R.string.share_content,
                    Formatter.redacted(redacted),
                    Formatter.redacted(redacted)
                )
            )
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
            Uri.parse(Formatter.redacted(BuildConfig.APP_SELFCHECK_URL))
        )
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
        labelDialog?.dismiss()
    }

    private fun checkAutostart() {
        if (autoStart.isAutoStartPermissionAvailable(this)) {
            if (Hawk.get(Constant.CHECK_AUTOSTART_PERMISSION, true)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.autostart_request))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.understand)) { _, _ ->
                        val success = autoStart.getAutoStartPermission(this@MainActivity)
                        /*if (!success) {
                            MaterialAlertDialogBuilder(this)
                                .setTitle(getString(R.string.oops))
                                .setMessage(getString(R.string.autostart_manual))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.understand)) { _, _ ->
                                    startActivity(
                                        Intent(
                                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse(
                                                "package:$packageName"
                                            )
                                        )
                                    )
                                }
                                .setNegativeButton(getString(R.string.ignore)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }*/
                    }
                    .show()
                Hawk.put(Constant.CHECK_AUTOSTART_PERMISSION, false)
            }
        }
    }

    fun showStats(view: View) {
        startActivity<InformationActivity>()
    }

    private fun checkRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                checkForUpdate()
            }
        }
    }

    private fun checkForUpdate() {
        if (!Hawk.contains(Constant.STORAGE_MAC_ADDRESS)) return
        val latestAppVersion = remoteConfig.getDouble(Constant.MIN_VERSION)
        if (latestAppVersion > BuildConfig.VERSION_CODE.toDouble()) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.title_update))
                .setMessage(getString(R.string.title_update_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.understand)) { _, _ ->
                    val appPackageName = packageName
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")
                            )
                        )
                    } catch (err: android.content.ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                            )
                        )
                    }
                }
                .setNegativeButton(getString(R.string.ignore)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun checkArea() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        fusedLocationClient.lastLocation.addOnCompleteListener {
            val location: Location? = it.result
            if (location != null) {
                if (location.isFromMockProvider) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.oops))
                        .setMessage(getString(R.string.fake_gps_message))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.exit)) { _, _ ->
                            finish()
                        }
                        .show()
                } else {
                    val geocoder = Geocoder(this)
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    val areas = remoteConfig.getString("area")
                    if (areas.isNotEmpty()) {
                        val parsed = JsonParser().parse(areas).asJsonObject.getAsJsonArray("partners")
                        val partners = Gson().fromJson<List<String>>(
                            parsed,
                            object : TypeToken<List<String>>() {}.type
                        )
                        addresses?.first()?.let {address ->
                            address.subAdminArea?.toLowerCase()?.split(' ')?.forEach { k ->
                                if(partners.contains(k)) {
                                    //Run Service
                                    Log.d(TAG, "PARTNER AREA")
                                    startService<LocationService>()
                                    startService<PingService>()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
