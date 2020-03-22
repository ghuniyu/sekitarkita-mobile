package id.ghuniyu.sekitar.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.MacAddress
import android.util.Log
import retrofit2.Callback
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import id.ghuniyu.sekitar.data.request.StoreDeviceRequest
import id.ghuniyu.sekitar.data.response.BaseResponse
import id.ghuniyu.sekitar.utils.MacAddressRetriever
import retrofit2.Call
import retrofit2.Response

class BluetoothReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "BluetoothReceiver"
        fun deviceToString(device: BluetoothDevice): String {
            return "[Address: " + device.address + ", Name: " + device.name + "]"
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "Incoming intent : $action")
        when (action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.d(TAG, "Device discovered! " + deviceToString(device))
                Client.service.postStoreDevice(
                    StoreDeviceRequest(
                        MacAddressRetriever.getBluetoothAddress(),
                        device.address,
                        null,
                        null
                    )
                ).enqueue(object : Callback<BaseResponse> {
                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        Log.w(TAG, t.localizedMessage)
                    }

                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.success!!) {
                                Log.i(TAG, response.body()?.message)
                            } else {
                                Log.w(TAG, response.body()?.message)
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

}