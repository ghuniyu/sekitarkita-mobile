package com.linkensky.ornet.data.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.math.BigInteger

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
    val updated_at: String,
    val app_user: Boolean = false
)

data class InteractionHistoryResponse(
    val nearbies: List<Interaction>
)

data class Device(
    val id: String,
    val name: String? = null,
    val user_status: String,
    val created_at: String,
    val updated_at: String
)

data class StoreDeviceResponse(
    val nearby_device: Device? = null
)

data class StoreLocationResponse(
    val zone: String? = null
)

data class Banner(
    val created_at: String,
    val description: String,
    val id: Int,
    val logo: String,
    val name: String,
    val updated_at: String
)

data class GtoStatisticResponse(
    val diUpdate: String,
    val data: List<GtoData>,
    val show: String
)

data class GtoStatus(
    val id: Int,
    val name: String,
    val orangs_count: Int,
    val tipe_id: Int
)

data class GtoData(
    val id: Int,
    val name: String,
    val statuses: List<GtoStatus>
)

data class Area(
    val id: BigInteger,
    val name: String
)