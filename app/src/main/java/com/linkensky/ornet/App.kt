package com.linkensky.ornet

import androidx.multidex.MultiDexApplication
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxViewModelConfigFactory
import com.linkensky.ornet.data.networkModule
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        Hawk.init(this).build()

        MvRx.viewModelConfigFactory = MvRxViewModelConfigFactory(applicationContext)

        startKoin {
            androidContext(this@App)
            modules(networkModule)
        }
    }
}