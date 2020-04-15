package com.linkensky.ornet.service

import android.location.Address
import android.util.Log
import com.linkensky.ornet.data.callback.DoNothingCallback
import com.linkensky.ornet.data.remote.Client
import com.linkensky.ornet.data.request.StoreLocationRequest
import com.linkensky.ornet.utils.Constant
import com.orhanobut.hawk.Hawk
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PingService : BaseService() {

    override fun onStartCommand() {
        Log.d("PingService", "Service Started")
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            ping()
        }, 0, 30, TimeUnit.MINUTES)
    }

    private fun ping()
    {
        val lastKnownLatitude = Hawk.get<Double>(Constant.STORAGE_LASTKNOWN_LAT)
        val lastKnownLongitude = Hawk.get<Double>(Constant.STORAGE_LASTKNOWN_LNG)
        val speed = Hawk.get<Float>(Constant.STORAGE_LASTKNOWN_SPEED)
        val deviceId = Hawk.get<String>(Constant.STORAGE_MAC_ADDRESS)
        val area = Hawk.get<String?>(Constant.STORAGE_LASTKNOWN_ADDRESS)

        Log.d("WhatsNull", "$deviceId != null && $lastKnownLatitude != null && $lastKnownLongitude != null && $area != null")
        if (deviceId != null && lastKnownLatitude != null && lastKnownLongitude != null && area != null) {
            Log.d(PING_SERVICE, MESSAGE)
            ping(lastKnownLatitude, lastKnownLongitude, speed, deviceId, area)
        }
    }

    private fun ping(latitude: Double, longitude: Double, speed: Float, device_id: String, area: String?) {
        area?.let {
            val request = StoreLocationRequest(
                latitude = latitude,
                longitude = longitude,
                speed = speed,
                device_id = device_id,
                area = it
            )
            Client.service.postStoreLocation(request).enqueue(object : DoNothingCallback(){})
        }
    }

    companion object {
        const val PING_SERVICE = "PING_SERVICE"
        const val MESSAGE = "REQUEST_PING"
    }

}
