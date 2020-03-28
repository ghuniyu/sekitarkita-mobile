package id.ghuniyu.sekitar.service

import Client
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.orhanobut.hawk.Hawk
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.request.StoreDeviceRequest
import id.ghuniyu.sekitar.data.response.StoreDeviceResponse
import id.ghuniyu.sekitar.ui.activity.ReportActivity
import id.ghuniyu.sekitar.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BluetoothReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "BluetoothReceiver"
        const val MINIMUM_SPEED = 4.6
        fun deviceToString(device: BluetoothDevice): String {
            return "[Address: " + device.address + ", Name: " + device.name + "]"
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "Incoming intent : $action")
        when (action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceType = getBluetoothType(device.bluetoothClass.majorDeviceClass)
                Log.d(TAG, "Device discovered! [${deviceType}] ${deviceToString(device)}")
                if (context != null) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnCompleteListener {
                        val location: Location? = it.result
                        if (location == null) {
                            requestNewLocationData(context)
                        } else {
                            Log.d(TAG, "speed: ${location.speed}")
                            Log.d(TAG, "latitude: ${location.latitude}")
                            Log.d(TAG, "longitude: ${location.longitude}")
                            Hawk.put(Constant.STORAGE_LATEST_SPEED, location.speed)
                            Hawk.put(Constant.STORAGE_LATEST_LAT, location.latitude)
                            Hawk.put(Constant.STORAGE_LATEST_LNG, location.longitude)
                        }
                    }
                }

                if (Hawk.get(Constant.STORAGE_LATEST_SPEED, 0) > MINIMUM_SPEED) return

                //FIXME: select specifics allowed type
                val allowedType:IntArray = intArrayOf(
                    BluetoothClass.Device.Major.AUDIO_VIDEO,
                    BluetoothClass.Device.Major.COMPUTER,
                    BluetoothClass.Device.Major.HEALTH,
                    BluetoothClass.Device.Major.MISC,
                    BluetoothClass.Device.Major.PHONE,
                    BluetoothClass.Device.Major.UNCATEGORIZED,
                    BluetoothClass.Device.Major.WEARABLE
                )
                if (!allowedType.contains(device.bluetoothClass.majorDeviceClass)) return

                Client.service.postStoreDevice(
                    StoreDeviceRequest(
                        Hawk.get(Constant.STORAGE_MAC_ADDRESS),
                        device.address,
                        Hawk.get(Constant.STORAGE_LATEST_LAT),
                        Hawk.get(Constant.STORAGE_LATEST_LNG),
                        Hawk.get(Constant.STORAGE_LATEST_SPEED)
                    )
                ).enqueue(object : Callback<StoreDeviceResponse> {
                    override fun onFailure(call: Call<StoreDeviceResponse>, t: Throwable) {
                        Log.w(TAG, t.localizedMessage)
                    }

                    override fun onResponse(
                        call: Call<StoreDeviceResponse>,
                        response: Response<StoreDeviceResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.success!!) {
                                Log.i(TAG, response.body()?.message)
                            } else {
                                Log.w(TAG, response.body()?.message)
                                if (context != null && response.body()?.nearby_device != null) {
                                    val nearbyDevice = response.body()?.nearby_device
                                    if (nearbyDevice?.health_condition?.equals(ReportActivity.Health.HEALTHY) != true)
                                        showNotification(context, nearbyDevice?.health_condition?.toUpperCase() ?: "")
                                }
                            }
                        }
                    }

                })
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                Log.d(TAG, "Discovery ended.")
            }
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                Log.d(TAG, "Bluetooth state changed.")
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            Log.d(TAG, "latitude: ${mLastLocation.latitude}")
            Log.d(TAG, "longitude: ${mLastLocation.longitude}")
            Hawk.put(Constant.STORAGE_LATEST_LAT, mLastLocation.latitude)
            Hawk.put(Constant.STORAGE_LATEST_LNG, mLastLocation.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(context: Context) {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun showNotification(context: Context, label: String) {
        val notification = NotificationCompat.Builder(context, Constant.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Perhatian...!")
            .setContentText("Di sekitar Anda ada ${label}")
            .setSmallIcon(R.drawable.ic_bacteria)
            .setChannelId(Constant.NOTIFICATION_CHANNEL_ID)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Constant.NOTIFICATION_ALERT_ID, notification);
    }

    private fun getBluetoothType(type: Int): String {
        when(type) {
            BluetoothClass.Device.Major.AUDIO_VIDEO -> return "AUDIO_VIDEO"
            BluetoothClass.Device.Major.COMPUTER -> return "COMPUTER"
            BluetoothClass.Device.Major.HEALTH -> return "HEALTH"
            BluetoothClass.Device.Major.IMAGING -> return "IMAGING"
            BluetoothClass.Device.Major.MISC -> return "MISC"
            BluetoothClass.Device.Major.NETWORKING -> return "NETWORKING"
            BluetoothClass.Device.Major.PERIPHERAL -> return "PERIPHERAL"
            BluetoothClass.Device.Major.PHONE -> return "PHONE"
            BluetoothClass.Device.Major.TOY -> return "TOY"
            BluetoothClass.Device.Major.UNCATEGORIZED -> return "UNCATEGORIZED"
            BluetoothClass.Device.Major.WEARABLE -> return "WEARABLE"
        }
        return "UNKNOWN"
    }
}