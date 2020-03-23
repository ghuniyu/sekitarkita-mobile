package id.ghuniyu.sekitar.service

import Client
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.orhanobut.hawk.Hawk
import id.ghuniyu.sekitar.data.request.StoreDeviceRequest
import id.ghuniyu.sekitar.data.response.BaseResponse
import id.ghuniyu.sekitar.utils.Constant
import id.ghuniyu.sekitar.utils.MacAddressRetriever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext

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
                        Hawk.get(Constant.STORAGE_MAC_ADDRESS),
                        device.address,
                        Hawk.get(Constant.STORAGE_LATEST_LAT),
                        Hawk.get(Constant.STORAGE_LATEST_LNG)
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