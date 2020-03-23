package id.ghuniyu.sekitar.data.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SetHealthRequest(
    val device_id: String,
    val health: String
) : Parcelable