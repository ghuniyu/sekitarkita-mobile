package com.linkensky.ornet.presentation.information.sikm

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.linkensky.ornet.data.model.RequestDataSIKM
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.base.MvRxViewModel
import com.linkensky.ornet.utils.rxApi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import java.math.BigInteger


class CreateSikmViewModel(
    state: CreateSikmState,
    val service: SekitarKitaService
) : MvRxViewModel<CreateSikmState>(state) {

    init {
        getGorontaloArea()
        getOriginCities()
    }

    fun setDate(date: String) = setState {
        copy(medical_issued = date)
    }

    fun setName(value: String) = setState { copy(name = value) }
    fun setCategory(value: String) = setState { copy(category = value) }
    fun setNik(value: String) = setState { copy(nik = value) }
    fun setOriginId(value: BigInteger) = setState { copy(originable_id = value) }
    fun setDestinationId(value: BigInteger) = setState { copy(destinable_id = value) }
    fun setPhone(value: String) = setState { copy(phone = value) }

    fun setKtpFile(file: MultipartBody.Part) = setState { copy(ktp_file = file) }

    fun setMedicalFile(file: MultipartBody.Part) = setState { copy(medical_file = file) }

    fun getGorontaloArea() = viewModelScope.rxApi {
        service.gorontaloProvince().data
    }.execute { copy(gorontaloAreas = it) }

    fun getOriginCities() = viewModelScope.rxApi {
        service.originCities().data
    }.execute { copy(originCities = it) }

    fun setBottomSheet(number: Int) = setState { copy(sheetId = number) }

    fun setOriginText(value: String) = setState { copy(originText = value) }

    fun setDestinationText(value: String) = setState { copy(destinationText = value) }

    fun setOriginFilter(value: String) = setState { copy(originFilter = value) }

    fun setDestinationFilter(value: String) = setState { copy(destinationFilter = value) }

    fun storeData(data: RequestDataSIKM, ktp: MultipartBody.Part, medical: MultipartBody.Part) =
        viewModelScope.rxApi {
            service.postStoreSIKM(
                ktp_file = ktp,
                medical_file = medical,
                name = data.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                device_id = data.device_id.toRequestBody("text/plain".toMediaTypeOrNull()),
                nik = data.nik.toRequestBody("text/plain".toMediaTypeOrNull()),
                phone = data.phone.toRequestBody("text/plain".toMediaTypeOrNull()),
                originable_id = data.originable_id.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                destinable_id = data.destinable_id.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                category = data.category.toRequestBody("text/plain".toMediaTypeOrNull()),
                medical_issued = data.medical_issued.toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }.execute {
            copy(response = it)
        }

    companion object : MvRxViewModelFactory<CreateSikmViewModel, CreateSikmState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: CreateSikmState
        ): CreateSikmViewModel {
            val service: SekitarKitaService by viewModelContext.activity.inject()
            return CreateSikmViewModel(state, service)
        }
    }
}