package com.linkensky.ornet.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.linkensky.ornet.data.remote.Client
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import com.linkensky.ornet.R
import com.linkensky.ornet.data.callback.ChangeHealthCallback
import com.linkensky.ornet.data.callback.please
import com.linkensky.ornet.data.request.RequestStatusChange
import com.linkensky.ornet.data.response.BaseResponse
import com.linkensky.ornet.utils.Constant
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import retrofit2.Response


class ReportActivity : BaseActivity() {

    var address: String? = null
    var dialog: AlertDialog? = null
    override fun getLayout() = R.layout.activity_report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        address = Hawk.get(Constant.STORAGE_LASTKNOWN_ADDRESS)
        Log.d("Address", "Hawk $address")
    }

    enum class Health(val condition: String) {
        HEALTHY("healthy"),
        PDP("pdp"),
        ODP("odp"),
        CONFIRMED("confirmed"),
    }

    @SuppressLint("InflateParams")
    fun report(view: View) {
        val phone = layoutInflater.inflate(R.layout.input_phone, null)
        val phoneInput = phone.find<EditText>(R.id.input_phone)
        val nikInput = phone.find<EditText>(R.id.input_nik)
        val nameInput = phone.find<EditText>(R.id.input_name)
        val trips = phone.find<EditText>(R.id.trip_history)
        address?.let {
            nikInput.show()
            nameInput.show()
        }

        when (view.id) {
            R.id.healthy -> {
                dialog = MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_healthy))
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        postReport(Health.HEALTHY, null)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                dialog?.setOnShowListener {
                    val confirm = dialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm?.onClick {
                        if (address != null) {
                            nikInput.req("NIK harus diisi") {
                                nameInput.req("Nama harus diisi") {
                                    phoneInput.req(
                                        "Nomor HP harus diisi",
                                        Type.PHONE,
                                        "Nomor HP tidak sesuai"
                                    ) {
                                        trips.req("Riwayat Perjalanan Harus diisi") {
                                            postReport(
                                                Health.HEALTHY,
                                                phoneInput.text.toString(),
                                                nikInput.text.toString(),
                                                nameInput.text.toString(),
                                                trips.text.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            phoneInput.req(
                                "Nomor HP harus diisi",
                                Type.PHONE,
                                "Nomor HP tidak sesuai"
                            ) {
                                trips.req("Riwayat Perjalanan Harus diisi") {
                                    postReport(
                                        Health.HEALTHY,
                                        phoneInput.text.toString(),
                                        trips = trips.text.toString()
                                    )
                                }
                            }
                        }
                    }
                }
                dialog?.setView(phone)
                dialog?.show()
            }
            R.id.odp -> {
                dialog = MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_odp))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                dialog?.setOnShowListener {
                    val confirm = dialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm?.onClick {
                        if (address != null) {
                            nikInput.req("NIK harus diisi") {
                                nameInput.req("Nama harus diisi") {
                                    phoneInput.req(
                                        "Nomor HP harus diisi",
                                        Type.PHONE,
                                        "Nomor HP tidak sesuai"
                                    ) {
                                        trips.req("Riwayat Perjalanan Harus diisi") {
                                            postReport(
                                                Health.ODP,
                                                phoneInput.text.toString(),
                                                nikInput.text.toString(),
                                                nameInput.text.toString(),
                                                trips.text.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            phoneInput.req(
                                "Nomor HP harus diisi",
                                Type.PHONE,
                                "Nomor HP tidak sesuai"
                            ) {
                                trips.req("Riwayat Perjalanan Harus diisi") {
                                    postReport(
                                        Health.ODP,
                                        phoneInput.text.toString(),
                                        trips = trips.text.toString()
                                    )
                                }
                            }
                        }
                    }
                }
                dialog?.setView(phone)
                dialog?.show()
            }
            R.id.pdp -> {
                dialog = MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_pdp))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                dialog?.setOnShowListener {
                    val confirm = dialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm?.onClick {
                        if (address != null) {
                            nikInput.req("NIK harus diisi") {
                                nameInput.req("Nama harus diisi") {
                                    phoneInput.req(
                                        "Nomor HP harus diisi",
                                        Type.PHONE,
                                        "Nomor HP tidak sesuai"
                                    ) {
                                        trips.req("Riwayat Perjalanan Harus diisi") {
                                            postReport(
                                                Health.PDP,
                                                phoneInput.text.toString(),
                                                nikInput.text.toString(),
                                                nameInput.text.toString(),
                                                trips.text.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            phoneInput.req(
                                "Nomor HP harus diisi",
                                Type.PHONE,
                                "Nomor HP tidak sesuai"
                            ) {
                                trips.req("Riwayat Perjalanan Harus diisi") {
                                    postReport(
                                        Health.PDP,
                                        phoneInput.text.toString(),
                                        trips = trips.text.toString()
                                    )
                                }
                            }
                        }
                    }
                }
                dialog?.setView(phone)
                dialog?.show()
            }
            R.id.confirmed -> {
                dialog = MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_positive))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                dialog?.setOnShowListener {
                    val confirm = dialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm?.onClick {
                        if (address != null) {
                            nikInput.req("NIK harus diisi") {
                                nameInput.req("Nama harus diisi") {
                                    phoneInput.req(
                                        "Nomor HP harus diisi",
                                        Type.PHONE,
                                        "Nomor HP tidak sesuai"
                                    ) {
                                        trips.req("Riwayat Perjalanan Harus diisi") {
                                            postReport(
                                                Health.CONFIRMED,
                                                phoneInput.text.toString(),
                                                nikInput.text.toString(),
                                                nameInput.text.toString(),
                                                trips.text.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            phoneInput.req(
                                "Nomor HP harus diisi",
                                Type.PHONE,
                                "Nomor HP tidak sesuai"
                            ) {
                                trips.req("Riwayat Perjalanan Harus diisi") {
                                    postReport(
                                        Health.CONFIRMED,
                                        phoneInput.text.toString(),
                                        trips = trips.text.toString()
                                    )
                                }
                            }
                        }
                    }
                }
                dialog?.setView(phone)
                dialog?.show()
            }
        }
    }

    private fun postReport(
        health: Health,
        phone: String?,
        nik: String? = null,
        name: String? = null,
        trips: String? = null
    ) {
        Client.service.requestStatusChange(
            RequestStatusChange(
                Hawk.get(Constant.STORAGE_MAC_ADDRESS),
                health.condition,
                phone,
                nik,
                name,
                trips
            )
        ).enqueue(object : ChangeHealthCallback(this@ReportActivity) {
            override fun onSuccess(response: Response<BaseResponse>) {
                super.onSuccess(response)
                response.body()?.let { data ->
                    data.success?.let { success ->
                        if (success) {
                            Toasty.success(this@ReportActivity, data.message.please()).show()
                            dialog?.dismiss()
                        } else {
                            Toasty.error(this@ReportActivity, data.message.please()).show()
                        }
                    }
                }
            }
        })
    }
}