package id.ghuniyu.sekitar.data.remote

import id.ghuniyu.sekitar.data.model.Country
import id.ghuniyu.sekitar.data.model.Province
import retrofit2.Call
import retrofit2.http.GET

interface PublicService {
    @GET("indonesia")
    fun getIndonesia(): Call<List<Country>>

    @GET("indonesia/provinsi")
    fun getProvinces(): Call<List<Province>>
}