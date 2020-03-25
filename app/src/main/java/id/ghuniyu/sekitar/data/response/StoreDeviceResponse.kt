package id.ghuniyu.sekitar.data.response

import android.os.Parcelable
import id.ghuniyu.sekitar.data.model.Device
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreDeviceResponse(
    val nearby_device: Device
) : Parcelable, BaseResponse()