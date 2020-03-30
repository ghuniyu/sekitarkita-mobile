package com.linkensky.ornet.data.response

import android.os.Parcelable
import com.linkensky.ornet.data.model.Device
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreDeviceResponse(
    val nearby_device: Device
) : Parcelable, BaseResponse()