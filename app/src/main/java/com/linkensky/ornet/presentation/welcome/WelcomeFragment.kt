package com.linkensky.ornet.presentation.welcome

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentWelcomeBinding
import com.linkensky.ornet.presentation.base.*
import com.linkensky.ornet.presentation.home.BluetoothStateChanged
import com.linkensky.ornet.presentation.home.HomeFragment
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

class WelcomeFragment : BaseEpoxyFragment<FragmentWelcomeBinding>() {

    private val autoStart = CheckAutostartPermission.getInstance()

    override var fragmentLayout = R.layout.fragment_welcome
    override fun invalidate() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: BluetoothStateChanged) {
        if (!event.isEnable) {
            enableBluetooth()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            setOnStart {
                requestLocationPermission()
            }
        }
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(HomeFragment.TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                task.result?.let {
                    Hawk.put(Const.STORAGE_FIREBASE_TOKEN, it.token)
                    MessagingService.storeFirebaseToken()
                    Log.d(HomeFragment.TAG, "getInstanceId success ${it.token}")
                }
            })

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            enableBluetooth()
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withContext(context)
                .withPermissions(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        report?.let {
                            when {
                                it.areAllPermissionsGranted() -> enableBluetooth()
                                it.isAnyPermissionPermanentlyDenied -> MaterialAlertDialogBuilder(
                                    requireContext()
                                )
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
                                else -> MaterialAlertDialogBuilder(requireContext())
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

                    override fun onPermissionRationaleShouldBeShown(
                        list: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                })
                .withErrorListener {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                }.check()
        } else
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH) {
            enableBluetooth()
        }
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
                navigateTo(R.id.action_welcomeFragment_to_homeFragment)
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
            navigateTo(R.id.action_welcomeFragment_to_macAddressFragment)
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

    companion object {
        const val TAG = "HOME_FRAGMENT"
        const val REQUEST_BLUETOOTH = 2
        const val RESPONSE_BLUETOOTH_DENY = 0
        const val RESPONSE_BLUETOOTH_ACTIVE = -1
    }

    override fun epoxyController() = buildController {
        /*addModel(
            "bluetooth-status"
        )*/
    }
}