package id.ghuniyu.sekitar.data.request

import android.os.Parcelable
import id.ghuniyu.sekitar.data.model.Interaction
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InteractionHistoryRequest(
    val device_id: String
) : Parcelable