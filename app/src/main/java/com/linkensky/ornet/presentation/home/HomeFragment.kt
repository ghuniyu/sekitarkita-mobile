package com.linkensky.ornet.presentation.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.activityViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.event.BluetoothStateChanged
import com.linkensky.ornet.data.event.ZoneEvent
import com.linkensky.ornet.databinding.FragmentHomeBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.service.LocationService
import com.linkensky.ornet.service.MessagingService
import com.linkensky.ornet.service.ScanService
import com.linkensky.ornet.utils.CheckAutostartPermission
import com.linkensky.ornet.utils.MacAddressRetriever
import com.linkensky.ornet.utils.resString
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


open class HomeFragment : BaseEpoxyFragment<FragmentHomeBinding>() {

    override var fragmentLayout: Int = R.layout.fragment_home

    private val viewModel: HomeViewModel by activityViewModel()
    private val autoStart = CheckAutostartPermission.getInstance()

    private val controller by lazy {
        HomeController(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        Dexter.withContext(context)
            .withPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(grantedResponse: PermissionGrantedResponse?) {
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
        } else {
            bluetoothOn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "RequestCode: $requestCode | ResponseCode $resultCode")
        if (requestCode == REQUEST_BLUETOOTH && resultCode == RESPONSE_BLUETOOTH_DENY) {
            Toasty.error(requireContext(), Const.BLUETOOTH_DENY_MESSAGE)
                .show()
            requireActivity().finish()
        }else if (requestCode == REQUEST_BLUETOOTH && resultCode == RESPONSE_BLUETOOTH_ACTIVE) {
            bluetoothOn()
        }
    }

    private fun bluetoothOn() {
        Log.d(TAG, "Bt On ${Hawk.contains(Const.DEVICE_ID)}")
        if (Hawk.contains(Const.DEVICE_ID)) {
            if (Hawk.contains(Const.SELF_TEST_COMPLETED)) {
                checkAutostart()
                Log.d(TAG, getString(R.string.bluetooth_active))
                requireActivity().startService(Intent(requireActivity(), ScanService::class.java))
                requireActivity().startService(
                    Intent(
                        requireActivity(),
                        LocationService::class.java
                    )
                )
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.self_check))
                    .setMessage(getString(R.string.self_check_message))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.understand)) { _, _ ->
                        navigateTo(R.id.action_homeFragment_to_selfcheckFragment)
                    }
                    .setNegativeButton(getString(R.string.exit)) { _, _ ->
                        activity?.finish()
                    }
                    .setIcon(R.mipmap.ic_launcher)
                    .show()
            }
        } else {
            retrieveMac()
        }
    }

    private fun retrieveMac() {
        val mac = MacAddressRetriever.getBluetoothAddress()
        if (mac == "") {
            navigateTo(R.id.action_homeFragment_to_macAddressFragment)
            return
        } else {
            Hawk.put(Const.DEVICE_ID, mac)
            Log.d(TAG, "retrieveMac")
            bluetoothOn()
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

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
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