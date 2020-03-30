package com.linkensky.ornet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device(
    val id: String,
    val health_condition: String,
    val created_at: String,
    val updated_at: String
) : Parcelable