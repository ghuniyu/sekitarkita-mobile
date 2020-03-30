package com.linkensky.ornet.ui.activity

import Client
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import com.linkensky.ornet.R
import com.linkensky.ornet.data.callback.SetHealthCallback
import com.linkensky.ornet.data.request.SetHealthRequest
import com.linkensky.ornet.utils.Constant

class ReportActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_report

    enum class Health(val condition: String) {
        HEALTHY("healthy"),
        PDP("pdp"),
        ODP("odp"),
        CONFIRMED("confirmed"),
    }

    fun report(view: View) {
        when (view.id) {
            R.id.healthy -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_healthy))
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        postReport(Health.HEALTHY)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.odp -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_odp))
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        postReport(Health.ODP)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.pdp -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_pdp))
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        postReport(Health.PDP)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.confirmed -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_positive))
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        postReport(Health.CONFIRMED)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        }
    }

    private fun postReport(health: Health) {
        Client.service.postSetHealth(
            SetHealthRequest(
                Hawk.get(Constant.STORAGE_MAC_ADDRESS),
                health.condition
            )
        ).enqueue(object : SetHealthCallback(this@ReportActivity) {})
    }
}