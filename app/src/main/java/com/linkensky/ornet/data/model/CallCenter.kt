package com.linkensky.ornet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CallCenter(
    val phone: String,
    val mobile: String?,
    val area: String,
    val area_detail: String,
    val website: String? = null
) : Parcelable