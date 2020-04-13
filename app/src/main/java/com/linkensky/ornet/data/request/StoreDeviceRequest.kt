package com.linkensky.ornet.data.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreDeviceRequest(
    val device_id: String,
    val nearby_device: String,
    val latitude: Double?,
    val longitude: Double?,
    val speed: Float?,
    val device_name: String?
) : Parcelable