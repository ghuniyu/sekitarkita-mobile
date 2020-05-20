package com.linkensky.ornet

import androidx.multidex.MultiDexApplication
import com.orhanobut.hawk.Hawk
import org.koin.core.context.startKoin

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        Hawk.init(this).build()

        startKoin {

        }
    }
}