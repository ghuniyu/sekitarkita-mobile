package id.ghuniyu.sekitar.data.remote

import id.ghuniyu.sekitar.data.request.InteractionHistoryRequest
import id.ghuniyu.sekitar.data.request.SetHealthRequest
import id.ghuniyu.sekitar.data.response.InteractionHistoryResponse
import id.ghuniyu.sekitar.data.response.SetHealthResponse
import retrofit2.Call
import retrofit2.http.*

interface Service {
    @GET("device-history")
    fun getInteractionHistory(@Body request: InteractionHistoryRequest): Call<InteractionHistoryResponse>

    @POST("set-health")
    fun postSetHealth(@Body request: SetHealthRequest): Call<SetHealthResponse>
}