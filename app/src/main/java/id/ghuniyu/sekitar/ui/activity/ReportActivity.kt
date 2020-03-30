package id.ghuniyu.sekitar.ui.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import id.ghuniyu.sekitar.data.remote.Client
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.callback.SetHealthCallback
import id.ghuniyu.sekitar.data.request.SetHealthRequest
import id.ghuniyu.sekitar.utils.Constant
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick

class ReportActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_report

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

        when (view.id) {
            R.id.healthy -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_healthy))
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        postReport(Health.HEALTHY, null)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            R.id.odp -> {
                val odp = MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_odp))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                odp.setOnShowListener {
                    val confirm = odp.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm.onClick {
                        val phoneNumber = phoneInput.text.toString()
                        if (phoneNumber.isPhone()) {
                            postReport(Health.ODP, phoneInput.text.toString())
                            odp.dismiss()
                        } else {
                            phoneInput.error = getString(R.string.wrong_phone)
                        }
                    }
                }
                odp.setView(phone)
                odp.show()
            }
            R.id.pdp -> {
                val pdp = MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_pdp))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                pdp.setOnShowListener {
                    val confirm = pdp.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm.onClick {
                        val phoneNumber = phoneInput.text.toString()
                        if (phoneNumber.isPhone()) {
                            postReport(Health.PDP, phoneInput.text.toString())
                            pdp.dismiss()
                        } else {
                            phoneInput.error = getString(R.string.wrong_phone)
                        }
                    }
                }
                pdp.setView(phone)
                pdp.show()
            }
            R.id.confirmed -> {
                val confirmed = MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.confirm_positive))
                    .setPositiveButton(getString(R.string.confirm), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                confirmed.setOnShowListener {
                    val confirm = confirmed.getButton(AlertDialog.BUTTON_POSITIVE)
                    confirm.onClick {
                        val phoneNumber = phoneInput.text.toString()
                        if (phoneNumber.isPhone()) {
                            postReport(Health.CONFIRMED, phoneInput.text.toString())
                            confirmed.dismiss()
                        } else {
                            phoneInput.error = getString(R.string.wrong_phone)
                        }
                    }
                }
                confirmed.setView(phone)
                confirmed.show()
            }
        }
    }

    private fun postReport(health: Health, phone: String?) {
        Client.service.postSetHealth(
            SetHealthRequest(
                Hawk.get(Constant.STORAGE_MAC_ADDRESS),
                health.condition,
                phone
            )
        ).enqueue(object : SetHealthCallback(this@ReportActivity) {})
    }
}