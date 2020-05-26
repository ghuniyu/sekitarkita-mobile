package com.linkensky.ornet.presentation.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.activityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentHomeBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = GridLayoutManager(context, 1)
        }
        requestLocationPermission()
    }

    override fun epoxyController() = controller

    private fun requestLocationPermission() {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            enableBluetooth()
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

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
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

    private fun enableBluetooth() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()

        if (btAdapter === null) {
            Toasty.error(requireContext(), getString(R.string.bluetooth_unavailable)).show()
            requireActivity().finish()
            return
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

        if (requestCode == REQUEST_BLUETOOTH && resultCode == RESPONSE_BLUETOOTH_DENY) {
            Toasty.error(requireContext(), Const.BLUETOOTH_DENY_MESSAGE)
                .show()
            requireActivity().finish()
        }
    }

    private fun bluetoothOn() {
        if (Hawk.contains(Const.DEVICE_ID)) {
            if(Hawk.contains(Const.SELF_TEST_COMPLETED)) {
                checkAutostart()
                Log.d(TAG, getString(R.string.bluetooth_active))
                requireActivity().startService(Intent(requireActivity(), ScanService::class.java))
            }else {
                navigateTo(R.id.action_homeFragment_to_selfcheckFragment)
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
//                        checkLabel()
                    }
                    .show()
                Hawk.put(Const.CHECK_AUTOSTART_PERMISSION, false)
            }
        }
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
    }
}