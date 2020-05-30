package com.linkensky.ornet.presentation.interaction.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentInteractionDetailBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.home.HomeViewModel
import com.linkensky.ornet.utils.Formatter

class InteractionDetailFragment : BaseFragment<FragmentInteractionDetailBinding>() {

    private val viewModel: HomeViewModel by existingViewModel()

    override fun getLayoutRes(): Int = R.layout.fragment_interaction_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
    }

    override fun invalidate() {}

    private fun setupBinding() {
        withState(viewModel) { state ->
            binding.apply {
                lifecycleOwner = viewLifecycleOwner

                setOnBack {
                    findNavController().popBackStack()
                }
                val interaction = state.interaction!!
                deviceId = interaction.another_device.toUpperCase()
                statusText =
                    if (interaction.app_user) getString(R.string.registered_device) else getString(
                        R.string.unknown_device
                    )

                descriptionText = getString(
                    R.string.interaction_description,
                    Formatter.datetify(interaction.created_at),
                    interaction.another_device.toUpperCase()
                )

                if (interaction.latitude != null && interaction.longitude != null) {
                    hiddenLocation = false

                    setOnClickBtn {
                        goToMaps(interaction.latitude, interaction.longitude)
                    }
                } else {
                    hiddenLocation = true
                }
            }
        }
    }

    private fun goToMaps(latitude: Double, longitude: Double) {
        val uri = "geo:${latitude},${longitude}?q=${latitude},${longitude}"
        val gmmIntentUri: Uri = Uri.parse(uri)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

}