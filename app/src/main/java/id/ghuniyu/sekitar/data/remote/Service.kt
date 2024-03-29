package id.ghuniyu.sekitar.data.remote

import id.ghuniyu.sekitar.data.model.CallCenter
import id.ghuniyu.sekitar.data.model.Hospital
import id.ghuniyu.sekitar.data.request.InteractionHistoryRequest
import id.ghuniyu.sekitar.data.request.SetHealthRequest
import id.ghuniyu.sekitar.data.request.StoreDeviceRequest
import id.ghuniyu.sekitar.data.request.StoreFirebaseTokenRequest
import id.ghuniyu.sekitar.data.response.*
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
}