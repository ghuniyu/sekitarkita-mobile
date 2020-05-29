package com.linkensky.ornet.presentation.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.activityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.linkensky.ornet.BuildConfig
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.event.BluetoothStateChanged
import com.linkensky.ornet.data.event.PingEvent
import com.linkensky.ornet.data.event.ZoneEvent
import com.linkensky.ornet.data.model.Address
import com.linkensky.ornet.databinding.FragmentHomeBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.service.LocationService
import com.linkensky.ornet.service.MessagingService
import com.linkensky.ornet.utils.CheckAutostartPermission
import com.linkensky.ornet.utils.resString
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


open class HomeFragment : BaseEpoxyFragment<FragmentHomeBinding>() {

    override var fragmentLayout: Int = R.layout.fragment_home

    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var remoteConfig: FirebaseRemoteConfig

    private val autoStart = CheckAutostartPermission.getInstance()

    private val controller by lazy {
        HomeController(viewModel)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "APAKAH MASUK SINI")
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = GridLayoutManager(context, 1)
        }
        requestLocationPermission()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                task.result?.let {
                    Hawk.put(Const.STORAGE_FIREBASE_TOKEN, it.token)
                    MessagingService.storeFirebaseToken()
                    Log.d(TAG, "getInstanceId success ${it.token}")
                }
            })

        checkRemoteConfig()

//        val powerManager = requireActivity().getSystemService(POWER_SERVICE) as PowerManager
//        powerManager.apply {
//            if (Build.VERSION.SDK_INT >= 23 && !isIgnoringBatteryOptimizations(requireContext().packageName)) {
//                MaterialAlertDialogBuilder(requireContext())
//                    .setTitle(getString(R.string.warning))
//                    .setMessage(getString(R.string.autostart_request))
//                    .setCancelable(false)
//                    .setPositiveButton(getString(R.string.understand)) { _, _ ->
//                        startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
//                    }
//                    .show()
//            }
//        }
    }

    override fun epoxyController() = controller

    private fun requestLocationPermission() {
        Log.d(TAG, "APAKAH MASUK SINI Permission")
        Dexter.withContext(context)
            .withPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(grantedResponse: PermissionGrantedResponse?) {
                    Log.d(TAG, "APAKAH MASUK Granted")
                    checkArea()
                    enableBluetooth()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse?) {
                    permissionDeniedResponse?.let {
                        if (it.isPermanentlyDenied) {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.location_permission.resString())
                                .setMessage(R.string.request_location_permission.resString())
                                .setPositiveButton(R.string.understand.resString()) { _, _ ->
                                    openSettings()
                                }
                                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                                    activity?.finish()
                                }
                                .setCancelable(false)
                                .setIcon(R.mipmap.ic_launcher)
                                .show()
                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.location_permission.resString())
                                .setMessage(R.string.request_location_permission.resString())
                                .setPositiveButton(R.string.understand.resString()) { _, _ ->
                                    requestLocationPermission()
                                }
                                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                                    activity?.finish()
                                }
                                .setCancelable(false)
                                .setIcon(R.mipmap.ic_launcher)
                                .show()
                        }
                    }
                }
            })
            .withErrorListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
            .check()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(event: BluetoothStateChanged) {
        if (!event.isEnable) {
            enableBluetooth()
        }
    }

    @Subscribe
    open fun onEvent(event: ZoneEvent) {
        viewModel.updateZone()
    }

    private fun enableBluetooth() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()

        if (btAdapter === null) {
            Toasty.error(requireContext(), getString(R.string.bluetooth_unavailable)).show()
            requireActivity().finish()
        }

        if (!btAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_BLUETOOTH)
        }else {
            checkAutostart()
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
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

    private fun checkAutostart() {
        if (autoStart.isAutoStartPermissionAvailable(requireContext())) {
            if (Hawk.get(Const.CHECK_AUTOSTART_PERMISSION, true)) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.autostart_request))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.understand)) { _, _ ->
                        autoStart.getAutoStartPermission(requireContext())
                    }
                    .show()
                Hawk.put(Const.CHECK_AUTOSTART_PERMISSION, false)
            }
        }
    }

    private fun checkForUpdate() {
        if (!Hawk.contains(Const.DEVICE_ID)) return
        val latestAppVersion = remoteConfig.getDouble(Const.MIN_VERSION)
        if (latestAppVersion > BuildConfig.VERSION_CODE.toDouble()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.title_update))
                .setMessage(getString(R.string.title_update_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.understand)) { _, _ ->
                    val appPackageName = requireContext().packageName
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
        if (!Hawk.contains(Const.DEVICE_ID)) return
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocation.lastLocation.addOnCompleteListener {
                val location: Location? = it.result
                if (location != null) {
                    if (location.isFromMockProvider) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(getString(R.string.oops))
                            .setMessage(getString(R.string.fake_gps_message))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.exit)) { _, _ ->
                                requireActivity().finish()
                            }
                            .show()
                    } else {
                        val geoCoder = Geocoder(requireContext())
                        val addresses =
                            geoCoder.getFromLocation(location.latitude, location.longitude, 1)

                        val areas = remoteConfig.getString("area")
                        if (areas.isNotEmpty()) {
                            val parsed =
                                JsonParser().parse(areas).asJsonObject.getAsJsonArray("partners")
                            val partners = Gson().fromJson<List<String>>(
                                parsed,
                                object : TypeToken<List<String>>() {}.type
                            )
                            addresses?.first()?.let { address ->
                                address.subAdminArea?.let { city ->
                                    val currentAddress = Address(
                                        village = address.subLocality,
                                        district = address.locality,
                                        city = address.subAdminArea,
                                        province = address.adminArea
                                    )

                                    currentAddress.toString().toLowerCase(Locale.ROOT)
                                        .split(' ', ',').forEach { k ->
                                        if (partners.contains(k)) {
                                            Hawk.put(Const.IN_OBSERVE_AREA, true)
                                            if (Hawk.get(Const.NOTIFY_OBSERVE_AREA, true)) {
                                                Hawk.put(Const.NOTIFY_OBSERVE_AREA, false)
                                                MaterialAlertDialogBuilder(requireContext())
                                                    .setTitle(getString(R.string.observe_area))
                                                    .setMessage(
                                                        getString(
                                                            R.string.observe_area_info,
                                                            address.subAdminArea
                                                        )
                                                    )
                                                    .setCancelable(false)
                                                    .setPositiveButton(getString(R.string.understand)) { d, _ ->
                                                        d.dismiss()
                                                    }
                                                    .show()
                                            }
                                            Hawk.put(Const.STORAGE_LASTKNOWN_LAT, location.latitude)
                                            Hawk.put(Const.STORAGE_LASTKNOWN_LNG, location.longitude)
                                            Hawk.put(
                                                Const.STORAGE_LASTKNOWN_ADDRESS,
                                                currentAddress
                                            )
                                            EventBus.getDefault().post(PingEvent())
                                            requireActivity().startService(
                                                Intent(
                                                    requireActivity(),
                                                    LocationService::class.java
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        checkArea()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val TAG = "HOME_FRAGMENT"
        const val REQUEST_BLUETOOTH = 2
        const val RESPONSE_BLUETOOTH_DENY = 0
        const val RESPONSE_BLUETOOTH_ACTIVE = -1
    }
} 