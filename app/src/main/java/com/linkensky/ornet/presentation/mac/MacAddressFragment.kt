package com.linkensky.ornet.presentation.mac

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.existingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentMacAddressBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.home.HomeViewModel
import com.linkensky.ornet.utils.resString
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty

class MacAddressFragment : BaseFragment<FragmentMacAddressBinding>() {
    override fun getLayoutRes() = R.layout.fragment_mac_address

    private val viewModel: HomeViewModel by existingViewModel()

    private val controller by lazy {
        MacAddressController()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            tutorialRecycler.setController(controller)
            tutorialRecycler.layoutManager = GridLayoutManager(context, 1)
            text = "Bluetooth Perangkat"
            subTitle = "Lihat penduan pengguna dibawah"

            controller.requestModelBuild()
            setOnShowMac {
                startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS))
            }

            setOnSave {
                val macAddress = macEditText.text.toString()
                if (macAddress.length == 17)
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.are_you_sure))
                        .setMessage(getString(R.string.mac_address_confirm))
                        .setPositiveButton(getString(R.string.correct)) { _, _ ->
                            Hawk.put(Const.DEVICE_ID, macAddress)
                            Toasty.success(requireContext(), R.string.bluetooth_saved.resString())
                                .show()
                            hideSoftKey(context, view)
                            view.findNavController().navigate(R.id.action_macAddressFragment_to_selfcheckFragment)
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                else
                    macEditText.error = R.string.incorect_mac_address.resString()
            }
        }
    }

    override fun invalidate() {}

}