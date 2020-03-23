package id.ghuniyu.sekitar.data.callback

import android.content.Context
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.response.InteractionHistoryResponse
import retrofit2.Response

open class InteractionHistoryCallback(private val context: Context) :
    DefaultCallback<InteractionHistoryResponse>(context)