package id.ghuniyu.sekitar.ui.activity

import android.view.View
import id.ghuniyu.sekitar.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class ReportActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_report

    fun report(view: View) {
        when (view.id) {
            R.id.pdp -> {
                alert(
                    "Saya Menyatakan Bahwa Saya Benar Benar Pasien dalam Pengawasan",
                    "Apakah Anda Yakin?"
                ) {
                    yesButton {
                        toast("Terimakasih Laporan Anda telah Kami Terima")
                    }
                    noButton { }
                }.show()
            }
            R.id.odp -> {
                alert(
                    "Saya Menyatakan Bahwa Saya Benar Benar Orang dalam Pengawasan",
                    "Apakah Anda Yakin?"
                ) {
                    yesButton {
                        toast("Terimakasih Laporan Anda telah Kami Terima")
                    }
                    noButton { }
                }.show()
            }
            R.id.healthy -> {
                alert(
                    "Saya Menyatakan Bahwa Saya Benar Benar Sehat",
                    "Apakah Anda Yakin?"
                ) {
                    yesButton {
                        toast("Terimakasih Laporan Anda telah Kami Terima")
                    }
                    noButton { }
                }.show()
            }
        }
    }
}