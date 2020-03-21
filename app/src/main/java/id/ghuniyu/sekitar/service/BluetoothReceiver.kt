package id.ghuniyu.sekitar.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothReceiver : BroadcastReceiver(){
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