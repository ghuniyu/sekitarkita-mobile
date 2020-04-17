package com.linkensky.ornet.data.callback

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class DoNothingCallback<T> : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {}
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (!response.isSuccessful) return
        onSuccess(response)
    }
    open fun onSuccess(response: Response<T>) {}
}