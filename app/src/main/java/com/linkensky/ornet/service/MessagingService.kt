package com.linkensky.ornet.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.model.FirebaseTokenRequest
import com.linkensky.ornet.data.services.SekitarKitaService
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MessagingService : FirebaseMessagingService() {

    private val service: SekitarKitaService by inject()

    companion object {
        private const val TAG = "MainActivityTag"
    }

    private fun storeFirebaseToken() {
        if (!(Hawk.contains(Const.DEVICE_ID) && Hawk.contains(Const.STORAGE_FIREBASE_TOKEN))) return

        Log.d(
            TAG,
            "storeFirebaseToken ${Hawk.contains(Const.DEVICE_ID)} ${Hawk.contains(Const.STORAGE_FIREBASE_TOKEN)}"
        )
        GlobalScope.launch {
            service.postFirebase(
                FirebaseTokenRequest(
                    Hawk.get(Const.DEVICE_ID),
                    Hawk.get(Const.STORAGE_FIREBASE_TOKEN)
                )
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