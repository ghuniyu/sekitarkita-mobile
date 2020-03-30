package com.linkensky.ornet.data.remote

import com.linkensky.ornet.data.model.Country
import com.linkensky.ornet.data.model.Province
import retrofit2.Call
import retrofit2.http.GET

interface PublicService {
    @GET("indonesia")
    fun getIndonesia(): Call<List<Country>>

    @GET("indonesia/provinsi")
    fun getProvinces(): Call<List<Province>>
}