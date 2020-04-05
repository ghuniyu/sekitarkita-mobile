package com.linkensky.ornet.data.callback

import android.content.Context
import android.widget.Toast
import com.linkensky.ornet.R
import es.dmoral.toasty.Toasty
import com.linkensky.ornet.ui.dialog.LoadingDialog
import com.linkensky.ornet.data.remote.Client
import com.linkensky.ornet.data.response.BaseErrorResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
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
            response.code() == 422 -> onInvalid(response)
            response.code() == 429 -> onToManyRequest(response.headers().get("retry-after"))
            response.code() == 500 -> onInternalServerError()
            else -> onError(response.errorBody().toString())
        }
        onSuccess(response)
    }

    private fun onInvalid(response: Response<T>) {
        val converter: Converter<ResponseBody, BaseErrorResponse> =
            Client.retrofit.responseBodyConverter(
                BaseErrorResponse::class.java, arrayOf()
            )
        response.errorBody()

        response.errorBody()?.let { r ->
            val parsed = converter.convert(r)
            /*parsed.message?.let {
                generalError = Toasty.error(context, it)
                generalError?.show()
            }*/
            parsed.errors?.let {
                for ((key, list) in it) {
                    list.forEach { errors ->
                        generalError = Toasty.error(context, "$key - $errors")
                        generalError?.show()
                    }
                }
            }
        }
    }

    open fun onSuccess(response: Response<T>) {
        loadingDialog.dismiss()
    }

    open fun onToManyRequest(retry: String?) {
        retry?.let {
            generalError =
                Toasty.error(context, "Terlalu cepat, silahkan coba lagi setelah $it detik")
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