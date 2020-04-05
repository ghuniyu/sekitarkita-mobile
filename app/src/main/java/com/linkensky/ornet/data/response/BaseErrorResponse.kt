package com.linkensky.ornet.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BaseErrorResponse(
    val errors: HashMap<String, List<String>>?,
    val message: String?
) : Parcelable