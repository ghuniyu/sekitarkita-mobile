package com.linkensky.ornet.presentation.information.sikm

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.Area
import com.linkensky.ornet.data.model.RequestDataSIKM
import com.orhanobut.hawk.Hawk

data class CreateSikmState(
    val data: RequestDataSIKM = RequestDataSIKM(
        device_id = Hawk.get(Const.DEVICE_ID),
        name = "",
        nik = "",
        phone = "",
        ktp_file = "",
        medical_issued = "",
        medical_file = ""
    ),
    val gorontaloAreas: Async<List<Area>> = Uninitialized,
    val originCities: Async<List<Area>> = Uninitialized,
    val sheetId: Int = 1,
    val originText: String = "",
    val destinationText: String = "",
    val originFilter: String = "",
    val destinationFilter: String = ""
) : MvRxState
