package com.linkensky.ornet.data.services

import com.linkensky.ornet.data.model.*
import retrofit2.http.GET

interface SekitarKitaService {
    @GET("hospitals")
    suspend fun getHospitals(): BaseCollectionResponse<List<Hospital>>

    @GET("call-centers")
    suspend fun getCallCenters(): BaseCollectionResponse<List<CallCenter>>

    @GET("indonesia-statistics")
    suspend fun getIndonesia(): List<Country>

    @GET("province-statistics")
    suspend fun getProvinces(): List<Province>
}

