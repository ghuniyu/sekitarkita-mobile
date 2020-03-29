package id.ghuniyu.sekitar.data.callback

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.ui.dialog.LoadingDialog
import id.ghuniyu.sekitar.utils.Formatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

open class DefaultCallback<T>(
    private val context: Context
) : Callback<T> {

    private lateinit var loadingDialog: LoadingDialog
    private var generalError: Toast? = null
    private var internalError: Toast? = null
    private var networkFailure: Toast? = null
    private var unknownFailure: Toast? = null

    init {
        if (::loadingDialog.isInitialized)
            loadingDialog.dismiss()
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
            response.code() == 429 -> onToManyRequest(response.headers().get("retry-after"))
            response.code() == 500 -> onInternalServerError()
            else -> onError(response.errorBody().toString())
        }
        onSuccess(response)
    }

    open fun onSuccess(response: Response<T>) {
        loadingDialog.dismiss()
    }

    open fun onToManyRequest(retry: String?) {
        retry?.let {
            generalError = Toasty.error(context, "Terlalu cepat, silahkan coba lagi setelah $it detik")
            generalError?.show()
        }
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