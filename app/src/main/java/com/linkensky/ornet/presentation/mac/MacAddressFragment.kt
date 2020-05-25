package com.linkensky.ornet.presentation.mac

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentMacAddressBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class MacAddressFragment : BaseFragment<FragmentMacAddressBinding>() {
    override fun getLayoutRes() = R.layout.fragment_mac_address

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
        }
    }

    override fun invalidate() {}

}