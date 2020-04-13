package com.linkensky.ornet.service

import android.app.Service
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.IBinder
import android.util.Log
import com.linkensky.ornet.data.remote.Client
import com.linkensky.ornet.data.request.StoreLocationRequest
import com.linkensky.ornet.utils.Constant
import com.orhanobut.hawk.Hawk
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PingService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            excecute()
        }, 0, 30, TimeUnit.MINUTES)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    private fun excecute()
    {
        val lastKnownLatitude = Hawk.get<Double>(Constant.STORAGE_LATEST_LAT)
        val lastKnownLongitude = Hawk.get<Double>(Constant.STORAGE_LATEST_LNG)
        val speed = Hawk.get<Float>(Constant.STORAGE_LATEST_SPEED)
        val deviceId = Hawk.get<String>(Constant.STORAGE_MAC_ADDRESS)
        val area = Hawk.get<Address>(Constant.STORAGE_LATEST_ADDRESS)

        if (deviceId != null && lastKnownLatitude != null && lastKnownLongitude != null && area != null) {
            Log.d(PING_SERVICE, MESSAGE)
            ping(lastKnownLatitude, lastKnownLongitude, speed, deviceId, area)
        }
    }

    private fun ping(latitude: Double, longitude: Double, speed: Float, device_id: String, area: Address) {
        val request = StoreLocationRequest(
            latitude = latitude,
            longitude = longitude,
            speed = speed,
            device_id = device_id,
            area = area.subAdminArea
        )

        Client.service.postStoreLocation(request).execute()
    }

    companion object {
        const val PING_SERVICE = "PING_SERVICE"
        const val MESSAGE = "REQUEST_PING"
    }

}
