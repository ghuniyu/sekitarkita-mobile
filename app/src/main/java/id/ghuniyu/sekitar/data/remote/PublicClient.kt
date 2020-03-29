package id.ghuniyu.sekitar.data.remote

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PublicClient(val url: String) {
    val service: PublicService
    private val gson: Gson = Gson()

    init {

        val gsonConverter = GsonConverterFactory.create(gson)

        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor {
                val requestBuilder = it.request().newBuilder()

                requestBuilder.addHeader("Accept", "application/json")

                it.proceed(requestBuilder.build())
            }
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(gsonConverter)
            .baseUrl(url)
            .client(client)
            .build()

        service = retrofit.create(PublicService::class.java)
    }
}