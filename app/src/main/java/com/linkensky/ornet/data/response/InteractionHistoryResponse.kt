package com.linkensky.ornet.data.response

import android.os.Parcelable
import com.linkensky.ornet.data.model.Interaction
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InteractionHistoryResponse(
    val nearbies: List<Interaction>
): Parcelable, BaseResponse()