package id.ghuniyu.sekitar.data.callback

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.ui.dialog.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

private val <T> T?.success: Boolean get() = false

open class DefaultCallback<T>(
    private val context: Context
) : Callback<T> {

    private lateinit var loadingDialog: LoadingDialog
    private var generalError: Toast? = null
    private var internalError: Toast? = null
    private var networkFailure: Toast? = null
    private var unknownFailure: Toast? = null

    init {
        onStart()
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        loadingDialog.dismiss()
        if (t is ConnectException || t is SocketTimeoutException) {
            networkFailure = Toasty.error(context, context.getString(R.string.network_failure))
            networkFailure?.show()
        } else {
            unknownFailure = Toasty.error(context, context.getString(R.string.unknown_failure))
            unknownFailure?.show()
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        loadingDialog.dismiss()
        when {
            response.isSuccessful -> onSuccess(response)
            response.code() == 500 -> onInternalServerError()
            else -> onError(response.errorBody().toString())
        }
        if (response.body().success) {
            onSuccess(response)
        } else {
            onFailed(response)
        }
    }

    open fun onSuccess(response: Response<T>) {
        loadingDialog.dismiss()
    }

    open fun onFailed(response: Response<T>) {
        loadingDialog.dismiss()
    }

    private fun onStart() {
        loadingDialog = LoadingDialog(context)
        loadingDialog.show()
    }

    open fun onError(message: String?) {
        loadingDialog.dismiss()
        message?.let {
            generalError = Toasty.error(context, it)
            generalError?.show()
        }
    }

    private fun onInternalServerError() {
        loadingDialog.dismiss()
        internalError = Toasty.warning(context, context.getString(R.string.internal_error))
        internalError?.show()
    }
}