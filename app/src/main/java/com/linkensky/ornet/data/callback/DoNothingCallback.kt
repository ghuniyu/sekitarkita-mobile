package com.linkensky.ornet.data.callback

import com.linkensky.ornet.data.response.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class DoNothingCallback : Callback<BaseResponse> {
    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {}
    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {}
}