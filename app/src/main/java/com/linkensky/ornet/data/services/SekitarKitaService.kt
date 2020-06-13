package com.linkensky.ornet.data.services

import com.linkensky.ornet.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface SekitarKitaService {
    @GET("hospitals")
    suspend fun getHospitals(): BaseCollectionResponse<List<Hospital>>

    @GET("call-centers")
    suspend fun getCallCenters(): BaseCollectionResponse<List<CallCenter>>

    @GET("indonesia-statistics")
    suspend fun getIndonesia(): List<Country>

    @GET("province-statistics")
    suspend fun getProvinces(): List<Province>

    @GET("gorontalo-statistics")
    suspend fun getGorontalo(): GtoStatisticResponse

    @POST("v2/store-selfcheck")
    suspend fun storeSelfCheck(@Body request: ReportDataRequest): BaseResponse

    @POST("device-history")
    suspend fun deviceHistories(@Body request: InteractionHistoryRequest): InteractionHistoryResponse

    @POST("change-status")
    suspend fun postChangeStatus(@Body request: ChangeStatusRequest): BaseResponse

    @POST("store-firebase-token")
    suspend fun postFirebase(@Body request: FirebaseTokenRequest): BaseResponse

    @POST("store-device")
    suspend fun postStoreDevice(@Body request: StoreDeviceRequest): StoreDeviceResponse

    @POST("partners")
    suspend fun postStoreLocation(@Body request: StoreLocationRequest): StoreLocationResponse

    @GET("partners")
    suspend fun getBanner(): BaseCollectionResponse<List<Banner>>

    @GET("area/origin-cities")
    suspend fun originCities(): BaseCollectionResponse<List<Area>>

    @GET("area/gorontalo")
    suspend fun gorontaloProvince(): BaseCollectionResponse<List<Area>>

    @Multipart
    @POST("partners/sikm")
    suspend fun postStoreSIKM(
        @Part ktp_file: MultipartBody.Part,
        @Part medical_file: MultipartBody.Part,
        @Part("device_id") device_id: RequestBody,
        @Part("nik") nik: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("originable_id") originable_id: RequestBody,
        @Part("destinationable_id") destinable_id: RequestBody,
        @Part("category") category: RequestBody,
        @Part("medical_issued") medical_issued: RequestBody
    ) : SikmResponse

}