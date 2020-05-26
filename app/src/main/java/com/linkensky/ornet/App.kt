package com.linkensky.ornet

import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxViewModelConfigFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.linkensky.ornet.di.appModule
import com.orhanobut.hawk.Hawk
import io.reactivex.plugins.RxJavaPlugins
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.lang.ref.WeakReference

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        Hawk.init(this).build()

        MvRx.viewModelConfigFactory = MvRxViewModelConfigFactory(applicationContext)

        mContext = WeakReference(applicationContext)

        RxJavaPlugins.setErrorHandler {throwable ->
            throwable.message?.let { it -> FirebaseCrashlytics.getInstance().log(it) }
        }

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            AppConfig.onConfigChanged(applicationContext, newConfig)
        } catch (e: Exception) {

        }
    }

    companion object {
        private var mContext: WeakReference<Context>? = null

        fun getContext(): Context {
            return mContext?.get() ?: throw Error("No Context")
        }
    }

    object AppConfig {

        var density = 1f
        var fontDensity = 1f

        fun onConfigChanged(context: Context, newConfiguration: Configuration?) {
            val configuration = newConfiguration ?: context.resources.configuration

            density = context.resources.displayMetrics.density
            fontDensity = context.resources.displayMetrics.scaledDensity
        }
    }
}