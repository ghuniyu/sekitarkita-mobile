package com.linkensky.ornet.data.callback

import android.content.Context
import com.linkensky.ornet.data.response.InteractionHistoryResponse

open class InteractionHistoryCallback(private val context: Context) :
    DefaultCallback<InteractionHistoryResponse>(context)