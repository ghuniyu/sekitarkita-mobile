package com.linkensky.ornet.di

import com.linkensky.ornet.data.provideSekitarKitaService
import com.linkensky.ornet.data.retrofit
import org.koin.dsl.module

val appModule = module {
    factory { retrofit() }
    single { provideSekitarKitaService(get()) }
}
