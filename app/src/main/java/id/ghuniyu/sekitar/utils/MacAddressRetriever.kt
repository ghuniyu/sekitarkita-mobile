package id.ghuniyu.sekitar.utils

import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.util.Log
import net.vidageek.mirror.dsl.Mirror

class MacAddressRetriever {
    companion object {
        private const val FAKE_MAC_ADDRESS = "02:00:00:00:00:00"
        private const val TAG = "MacAddressRetriever"

        /**
         * Use this function to get bluetooth mac address from reflection */
        fun getBluetoothAddress(): String {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            var address = bluetoothAdapter.address
            if (address == FAKE_MAC_ADDRESS && Build.VERSION.SDK_INT < Build.VERSION_CODES.O /* Oreo */) {
                Log.w(
                        TAG,
                        "bluetoothAdapter.getAddress() did not return the physical address"
                )

                val bluetoothManagerService: Any =
                        Mirror().on(bluetoothAdapter).get().field("mService")
                val internalAddress: Any =
                        Mirror().on(bluetoothManagerService).invoke().method("getAddress").withoutArgs()
                if (internalAddress !is String) {
                    Log.w(
                            TAG,
                            "Couldn't call bluetoothAdapter.mService.getAddress() using reflection"
                    )
                    return ""
                }
                address = internalAddress
            }

            if (address == FAKE_MAC_ADDRESS) {
                Log.w(TAG, "Android is actively blocking requests to get the MAC address")
                return ""
            }

            return address
        }
    }
}