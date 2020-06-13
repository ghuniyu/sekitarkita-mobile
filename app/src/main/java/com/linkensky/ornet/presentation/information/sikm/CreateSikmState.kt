package com.linkensky.ornet.presentation.information.sikm

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.Area
import com.linkensky.ornet.data.model.BaseResponse
import com.linkensky.ornet.data.model.RequestDataSIKM
import com.linkensky.ornet.data.model.SikmResponse
import com.orhanobut.hawk.Hawk
import okhttp3.MultipartBody
import java.math.BigInteger

data class CreateSikmState(
    val name: String = "",
    val phone: String = "",
    val nik: String = "",
    val medical_issued: String = "",
    val originable_id: BigInteger? = null,
    val destinable_id: BigInteger? = null,
    val category: String = "one_way",
    val gorontaloAreas: Async<List<Area>> = Uninitialized,
    val originCities: Async<List<Area>> = Uninitialized,
    val sheetId: Int = 1,
    val originText: String = "",
    val destinationText: String = "",
    val originFilter: String = "",
    val destinationFilter: String = "",
    val ktp_file: MultipartBody.Part? = null,
    val medical_file: MultipartBody.Part? = null,
    val response: Async<SikmResponse> = Uninitialized
) : MvRxState
