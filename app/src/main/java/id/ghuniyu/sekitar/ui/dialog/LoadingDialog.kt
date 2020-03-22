package id.ghuniyu.sekitar.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import id.ghuniyu.sekitar.R

class LoadingDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.dialog_loading)
        window?.setBackgroundDrawable(null)
        window!!.setLayout(WRAP_CONTENT, WRAP_CONTENT)
    }
}