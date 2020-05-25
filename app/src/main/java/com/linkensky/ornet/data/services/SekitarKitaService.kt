package com.linkensky.ornet.data.services

import com.linkensky.ornet.data.model.BaseCollectionResponse
import com.linkensky.ornet.data.model.CallCenter
import com.linkensky.ornet.data.model.Hospital
import retrofit2.http.GET

interface SekitarKitaService {
    @GET("hospitals")
    suspend fun getHospitals(): BaseCollectionResponse<List<Hospital>>

    @GET("call-centers")
    suspend fun getCallCenters(): BaseCollectionResponse<List<CallCenter>>
}

