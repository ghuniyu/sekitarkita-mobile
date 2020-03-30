package id.ghuniyu.sekitar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Hospital(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String,
    val area: String,
    val area_detail: String,
    val latitude: Double?,
    val longitude: Double?
) : Parcelable