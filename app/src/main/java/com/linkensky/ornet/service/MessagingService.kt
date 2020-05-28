package com.linkensky.ornet.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.FirebaseTokenRequest
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.utils.rxApi
import com.orhanobut.hawk.Hawk
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.inject

class MessagingService : FirebaseMessagingService() {

    companion object : KoinComponent {
        private const val TAG = "MessagingService"
        private val service: SekitarKitaService by inject()

        fun storeFirebaseToken() {
            if (!(Hawk.contains(Const.DEVICE_ID) && Hawk.contains(Const.STORAGE_FIREBASE_TOKEN))) return
            Log.d(
                TAG,
                "storeFirebaseToken ${Hawk.contains(Const.DEVICE_ID)} ${Hawk.contains(Const.STORAGE_FIREBASE_TOKEN)}"
            )
            GlobalScope.rxApi {
                service.postFirebase(
                    FirebaseTokenRequest(
                        Hawk.get(Const.DEVICE_ID),
                        Hawk.get(Const.STORAGE_FIREBASE_TOKEN)
                    )
                )
            }.subscribeBy(
                onSuccess = {
                    it.message?.let { msg ->
                        Log.i(TAG, msg)
                    }
                },
                onError = {
                    Log.d(TAG, it.toString())
                }
            )
        }
    }

    override fun onNewToken(token: String) {
        if (!Hawk.isBuilt())
            Hawk.init(applicationContext).build()

        Log.d(TAG, "Refreshed token: $token")
        Hawk.put(Const.STORAGE_FIREBASE_TOKEN, token)
        storeFirebaseToken()
    }
}