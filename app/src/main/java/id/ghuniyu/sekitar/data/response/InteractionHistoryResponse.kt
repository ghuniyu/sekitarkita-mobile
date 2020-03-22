package id.ghuniyu.sekitar.data.response

import android.os.Parcelable
import id.ghuniyu.sekitar.data.model.Interaction
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InteractionHistoryResponse(
    val nearbies: List<Interaction>
) : Parcelable, BaseResponse()