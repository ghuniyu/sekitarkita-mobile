package com.linkensky.ornet.data.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetMeRequest(
    val device_id: String
) : Parcelable