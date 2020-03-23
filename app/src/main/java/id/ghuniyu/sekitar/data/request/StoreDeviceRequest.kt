package id.ghuniyu.sekitar.data.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreDeviceRequest(
    val device_id: String,
    val nearby_device: String,
    val latitude: Double?,
    val longitude: Double?
) : Parcelable