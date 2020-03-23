package id.ghuniyu.sekitar.ui.activity

import Client
import android.view.View
import com.orhanobut.hawk.Hawk
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.callback.SetHealthCallback
import id.ghuniyu.sekitar.data.request.SetHealthRequest
import id.ghuniyu.sekitar.utils.Constant
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class ReportActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_report

    enum class Health(val condition: String) {
        HEALTHY("healthy"),
        PDP("pdp"),
        ODP("odp"),
    }

    fun report(view: View) {
        when (view.id) {
            R.id.healthy -> {
                alert(
                    "Saya Menyatakan Bahwa Saya Benar Benar Sehat",
                    getString(R.string.are_you_sure)
                ) {
                    yesButton {
                        postReport(Health.HEALTHY)
                    }
                    noButton { }
                }.show()
            }
            R.id.odp -> {
                alert(
                    "Saya Menyatakan Bahwa Saya Benar Benar Orang dalam Pengawasan",
                    getString(R.string.are_you_sure)
                ) {
                    yesButton {
                        postReport(Health.ODP)
                    }
                    noButton { }
                }.show()
            }
            R.id.pdp -> {
                alert(
                    "Saya Menyatakan Bahwa Saya Benar Benar Pasien dalam Pengawasan",
                    getString(R.string.are_you_sure)
                ) {
                    yesButton {
                        postReport(Health.PDP)
                    }
                    noButton { }
                }.show()
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