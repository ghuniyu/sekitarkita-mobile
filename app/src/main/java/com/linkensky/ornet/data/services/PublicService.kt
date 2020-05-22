package com.linkensky.ornet.data.services

import com.linkensky.ornet.data.Country
import com.linkensky.ornet.data.Province
import retrofit2.http.GET

interface PublicService {
    @GET("indonesia")
    suspend fun getIndonesia(): List<Country>

    @GET("indonesia/provinsi")
    suspend fun getProvinces(): List<Province>
}