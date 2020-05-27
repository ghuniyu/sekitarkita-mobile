package com.linkensky.ornet.service

import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.linkensky.ornet.Const
import com.linkensky.ornet.data.event.PingEvent
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.utils.rxApi
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.*
import kotlinx.coroutines.rx2.rxSingle
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class LocationService : BaseService() {
    var scheduleTaskExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)
    private val service: SekitarKitaService by inject()

    override fun onStartCommand() {
        Log.d("LocationService", "Service Started")
        if (!Hawk.isBuilt())
            Hawk.init(applicationContext).build()

        locationService()
        ping()

        scheduleTaskExecutor.scheduleWithFixedDelay({
            locationService()
        },0, 2, TimeUnit.MINUTES)

        scheduleTaskExecutor.scheduleWithFixedDelay({
            ping()
        }, 0, 15, TimeUnit.SECONDS)
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
                                    Log.d("LASTKNOWN_ADDRESS", "Putting ${it.adminArea}")
                                    Hawk.put(Const.STORAGE_LASTKNOWN_ADDRESS, it.adminArea)
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
        val area = Hawk.get<String?>(Const.STORAGE_LASTKNOWN_ADDRESS)

        Log.d(
            "WhatsNull",
            "$deviceId != null && $lastKnownLatitude != null && $lastKnownLongitude != null && $area != null"
        )
        if (deviceId != null && lastKnownLatitude != null && lastKnownLongitude != null && area != null) {
            Log.d(PING_SERVICE, MESSAGE)
            ping(lastKnownLatitude, lastKnownLongitude, deviceId, area)
        }
    }

    private fun ping(latitude: Double, longitude: Double, device_id: String, area: String?) {
        area?.let {
            EventBus.getDefault().post(
                PingEvent(
                    latitude = latitude,
                    longitude = longitude,
                    device_id = device_id,
                    area = area
                )
            )
        }
    }


    companion object {
        const val PING_SERVICE = "PING_SERVICE"
        const val MESSAGE = "REQUEST_PING"
    }

}