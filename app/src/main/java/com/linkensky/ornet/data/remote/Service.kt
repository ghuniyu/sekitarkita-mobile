package com.linkensky.ornet.data.remote

import com.linkensky.ornet.data.model.CallCenter
import com.linkensky.ornet.data.model.Hospital
import com.linkensky.ornet.data.request.*
import com.linkensky.ornet.data.response.*
import retrofit2.Call
import retrofit2.http.*

interface Service {
    @POST("device-history")
    fun postInteractionHistory(@Body request: InteractionHistoryRequest): Call<InteractionHistoryResponse>

    @POST("set-health")
    fun postSetHealth(@Body request: SetHealthRequest): Call<SetHealthResponse>

    @POST("store-device")
    fun postStoreDevice(@Body request: StoreDeviceRequest): Call<StoreDeviceResponse>

    @POST("store-firebase-token")
    fun postFirebaseToken(@Body request: StoreFirebaseTokenRequest): Call<BaseResponse>

    @GET("call-centers")
    fun getCallCenter(): Call<BaseCollectionResponse<List<CallCenter>>>

    @GET("hospitals")
    fun getHospital(): Call<BaseCollectionResponse<List<Hospital>>>

    @POST("partners")
    fun postStoreLocation(@Body request: StoreLocationRequest): Call<BaseResponse>
}