package com.linkensky.ornet.data

import com.linkensky.ornet.BuildConfig
import com.linkensky.ornet.data.services.PublicService
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.utils.Formatter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

const val sekitarRetrofit = "sekitarRetrofit";

val networkModule = module {
    factory(named(sekitarRetrofit)) { retrofit() }
    single { provideSekitarKitaService(get(named(sekitarRetrofit))) }
    factory { retrofit(Formatter.redacted(BuildConfig.APP_KAWALCRN_API)) }
    single { providePublicService(get()) }
}

fun retrofit(url: String = BuildConfig.APP_BASE_URL): Retrofit {

    val loggingInterceptor: HttpLoggingInterceptor = if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    } else {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
    }


    val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor {
            val requestBuilder = it.request().newBuilder()

            requestBuilder.addHeader("Accept", "application/json")

            it.proceed(requestBuilder.build())
        }

    return Retrofit.Builder()
        .baseUrl(url)
        .client(httpClient.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

fun provideSekitarKitaService(retrofit: Retrofit): SekitarKitaService =
    retrofit.create(SekitarKitaService::class.java)

fun providePublicService(retrofit: Retrofit): PublicService =
    retrofit.create(PublicService::class.java)
