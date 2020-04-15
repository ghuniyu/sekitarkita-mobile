package com.linkensky.ornet.data.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestStatusChange(
    val device_id: String,
    val health: String,
    val phone: String?,
    val nik: String?,
    val name: String?
) : Parcelable