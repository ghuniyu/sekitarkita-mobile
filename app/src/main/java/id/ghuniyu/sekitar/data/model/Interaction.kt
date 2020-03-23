package id.ghuniyu.sekitar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Interaction(
    val id: Int,
    val device_id: String,
    val another_device: String,
    val latitude: Double?,
    val longitude: Double?,
    val created_at: String,
    val updated_at: String
) : Parcelable