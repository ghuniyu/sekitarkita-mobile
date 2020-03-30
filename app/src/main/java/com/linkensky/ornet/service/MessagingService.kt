package com.linkensky.ornet.service

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.orhanobut.hawk.Hawk
import com.linkensky.ornet.data.callback.DefaultCallback
import com.linkensky.ornet.data.request.StoreFirebaseTokenRequest
import com.linkensky.ornet.data.response.BaseResponse
import com.linkensky.ornet.utils.Constant
import retrofit2.Call
import retrofit2.Response

class MessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MainActivityTag"

        fun storeFirebaseToken(context: Context) {
            Log.d(TAG, "storeFirebaseToken ${Hawk.contains(Constant.STORAGE_MAC_ADDRESS)} ${Hawk.contains(Constant.STORAGE_FIREBASE_TOKEN)}")
            if(!(Hawk.contains(Constant.STORAGE_MAC_ADDRESS) && Hawk.contains(Constant.STORAGE_FIREBASE_TOKEN))) return
            Client.service.postFirebaseToken(
                StoreFirebaseTokenRequest(
                    Hawk.get(Constant.STORAGE_MAC_ADDRESS),
                    Hawk.get(Constant.STORAGE_FIREBASE_TOKEN)
                )
            ).enqueue(object : DefaultCallback<BaseResponse>(context){
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    Log.d(TAG, "STORE RESPONSE ${response.message()}")
                    super.onResponse(call, response)
                }
            })
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        Hawk.put(Constant.STORAGE_FIREBASE_TOKEN, token)
        storeFirebaseToken(this@MessagingService)
    }
}