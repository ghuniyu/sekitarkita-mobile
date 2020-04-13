package com.linkensky.ornet.data.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreLocationRequest(
    val device_id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Float?,
    val area: String?
) : Parcelable