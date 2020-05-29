package com.linkensky.ornet.service

import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.event.PingEvent
import com.linkensky.ornet.data.model.Address
import com.linkensky.ornet.utils.toJson
import com.orhanobut.hawk.Hawk
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class LocationService : BaseService() {
    var scheduleTaskExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)

    override fun onStartCommand() {
        Log.d(TAG, "Service Started")
        if (!Hawk.isBuilt())
            Hawk.init(applicationContext).build()

        locationService()
        scheduleTaskExecutor.scheduleWithFixedDelay({
            ping()
        }, 0, 15, TimeUnit.MINUTES)
    }

    private fun locationService() {
        FusedLocationProviderClient(this).requestLocationUpdates(
            LocationRequest(),
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val lastLocation = result.lastLocation

                    if (!result.lastLocation.isFromMockProvider) {

                        Hawk.put(Const.STORAGE_LASTKNOWN_LAT, lastLocation.latitude)
                        Hawk.put(Const.STORAGE_LASTKNOWN_LNG, lastLocation.longitude)
                        Hawk.put(Const.STORAGE_LASTKNOWN_SPEED, lastLocation.speed)

                        val geoCoder = Geocoder(this@LocationService)

                        try {
                            val addresses = geoCoder.getFromLocation(
                                lastLocation.latitude,
                                lastLocation.longitude,
                                1
                            )

                            if (addresses.isNotEmpty() && Hawk.contains(Const.DEVICE_ID)) {
                                addresses.first()?.let {
                                    Log.d("LASTKNOWN_ADDRESS", "Address ${it.toJson()}")
                                    val address = Address(
                                        village = it.subLocality,
                                        district = it.locality,
                                        city = it.subAdminArea,
                                        province = it.adminArea
                                    )
                                    Hawk.put(Const.STORAGE_LASTKNOWN_ADDRESS, address)
                                }

                            }
                        } catch (e: IOException) {
                            //
                        }
                    }
                }
            },
            null
        )
    }

    private fun ping() {
        val lastKnownLatitude = Hawk.get<Double>(Const.STORAGE_LASTKNOWN_LAT)
        val lastKnownLongitude = Hawk.get<Double>(Const.STORAGE_LASTKNOWN_LNG)
        val deviceId = Hawk.get<String>(Const.DEVICE_ID)
        val area = Hawk.get<Address?>(Const.STORAGE_LASTKNOWN_ADDRESS)

        Log.d(
            "WhatsNull",
            "$deviceId != null && $lastKnownLatitude != null && $lastKnownLongitude != null && $area != null"
        )
        if (deviceId != null && lastKnownLatitude != null && lastKnownLongitude != null && area != null) {
            Log.d(TAG, MESSAGE)
            EventBus.getDefault().post(
                PingEvent()
            )
        }
    }

    companion object {
        const val TAG = "LocationService"
        const val MESSAGE = "REQUEST_PING"
    }

}