package com.linkensky.ornet.service

import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.linkensky.ornet.utils.Constant
import com.orhanobut.hawk.Hawk
import java.io.IOException

class LocationService : BaseService() {

    override fun onStartCommand() {
        Log.d("LocationService", "Service Started")
        if (!Hawk.isBuilt())
            Hawk.init(applicationContext).build()

        FusedLocationProviderClient(this).requestLocationUpdates(
            LocationRequest(),
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val lastLocation = result.lastLocation

                    if (!result.lastLocation.isFromMockProvider) {

                        Hawk.put(Constant.STORAGE_LASTKNOWN_LAT, lastLocation.latitude)
                        Hawk.put(Constant.STORAGE_LASTKNOWN_LNG, lastLocation.longitude)
                        Hawk.put(Constant.STORAGE_LASTKNOWN_SPEED, lastLocation.speed)

                        val geoCoder = Geocoder(this@LocationService)

                        try {
                            val addresses = geoCoder.getFromLocation(
                                lastLocation.latitude,
                                lastLocation.longitude,
                                1
                            )

                            if (addresses.isNotEmpty() && Hawk.contains(Constant.STORAGE_MAC_ADDRESS)) {
                                addresses.first()?.let {
                                    Log.d("LASTKNOWN_ADDRESS", "Putting ${it.subAdminArea}")
                                    Hawk.put(Constant.STORAGE_LASTKNOWN_ADDRESS, it.subAdminArea)
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

}