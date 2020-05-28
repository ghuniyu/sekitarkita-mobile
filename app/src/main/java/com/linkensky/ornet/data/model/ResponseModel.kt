package com.linkensky.ornet.data.model

data class Province(
    val attributes: Attributes
)

data class Country(
    val meninggal: String,
    val name: String,
    val positif: String,
    val sembuh: String
)

data class Attributes(
    val FID: Int,
    val Kasus_Meni: Int,
    val Kasus_Posi: Int,
    val Kasus_Semb: Int,
    val Kode_Provi: Int,
    val Provinsi: String
)

data class Hospital(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String,
    val area: String,
    val area_detail: String,
    val latitude: Double?,
    val longitude: Double?
)

data class CallCenter(
    val mobile: String,
    val phone: String,
    val website: String,
    val area: String,
    val area_detail: String
)

data class BaseCollectionResponse<T>(
    val data: T
) : BaseResponse()


open class BaseResponse(
    open val success: Boolean? = null,
    open val message: String? = null,
    open val stack_trace: String? = null
)

data class Interaction(
    val id: Int,
    val device_id: String,
    val another_device: String,
    val latitude: Double?,
    val longitude: Double?,
    val created_at: String,
    val updated_at: String
)

data class InteractionHistoryResponse(
    val nearbies: List<Interaction>
)

data class Device(
    val id: String,
    val health_condition: String,
    val created_at: String,
    val updated_at: String
)

data class StoreDeviceResponse(
    val nearby_device: Device? = null
)