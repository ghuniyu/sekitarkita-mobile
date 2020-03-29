package id.ghuniyu.sekitar.data.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreFirebaseTokenRequest(
    val device_id: String,
    val firebase_token: String
) : Parcelable