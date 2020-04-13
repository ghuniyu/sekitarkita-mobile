package com.linkensky.ornet.service

import android.content.Intent
import android.location.Geocoder
import android.os.IBinder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.linkensky.ornet.utils.Constant
import com.orhanobut.hawk.Hawk
import java.io.IOException

class LocationService : BaseService() {

    override fun onStartCommand() {
        FusedLocationProviderClient(this).requestLocationUpdates(
            LocationRequest(),
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val lastLocation = result.lastLocation

                    if (!result.lastLocation.isFromMockProvider) {

                        Hawk.put(Constant.STORAGE_LATEST_LAT, lastLocation.latitude)
                        Hawk.put(Constant.STORAGE_LATEST_LNG, lastLocation.longitude)
                        Hawk.put(Constant.STORAGE_LATEST_SPEED, lastLocation.speed)

                        val geocoder = Geocoder(this@LocationService)

                        try {
                            val addresses = geocoder.getFromLocation(
                                lastLocation.latitude,
                                lastLocation.longitude,
                                1
                            )

                            if (addresses.isNotEmpty() && Hawk.contains(Constant.STORAGE_MAC_ADDRESS)) {
                                Hawk.put(Constant.STORAGE_LATEST_ADDRESS, addresses.first())

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

}