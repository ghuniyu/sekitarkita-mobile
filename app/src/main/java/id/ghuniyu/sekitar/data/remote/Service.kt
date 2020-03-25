package id.ghuniyu.sekitar.data.remote

import id.ghuniyu.sekitar.data.request.InteractionHistoryRequest
import id.ghuniyu.sekitar.data.request.SetHealthRequest
import id.ghuniyu.sekitar.data.request.StoreDeviceRequest
import id.ghuniyu.sekitar.data.request.StoreFirebaseTokenRequest
import id.ghuniyu.sekitar.data.response.BaseResponse
import id.ghuniyu.sekitar.data.response.InteractionHistoryResponse
import id.ghuniyu.sekitar.data.response.SetHealthResponse
import id.ghuniyu.sekitar.data.response.StoreDeviceResponse
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
}