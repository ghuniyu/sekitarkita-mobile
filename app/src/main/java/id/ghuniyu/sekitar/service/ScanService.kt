package id.ghuniyu.sekitar.service

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import id.ghuniyu.sekitar.ui.activity.MainActivity
import id.ghuniyu.sekitar.R
import org.jetbrains.anko.toast
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class ScanService : Service() {

    private var btAdapter: BluetoothAdapter? = null
    var scheduleTaskExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)

    companion object {
        private const val EXTRA_ADDRESS = "Device_Address"
        private const val TAG = "BluetoothReceiver"
        const val REQUEST_COARSE = 1
        const val COARSE_GRANTED = "IsCoarseGranted"
        private const val REQUEST_BLUETOOTH = 2
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "SekitarNotification"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null) {
            toast("Perangkat Anda tidak memiliki Bluetooth")
            stopService(intent)
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(BluetoothReceiver(), filter)

        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = CHANNEL_ID
            notificationChannel.setSound(null, null)

            notificationManager.createNotificationChannel(notificationChannel)

            startForeground(
                NOTIFICATION_ID, NotificationCompat.Builder(this, CHANNEL_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_bacteria)
                    .setContentText("SekitarKita - sedang Berjalan")
                    .setContentIntent(pendingIntent)
                    .build()
            )
        }

        scheduleTaskExecutor.scheduleAtFixedRate({ nearbyDevice() }, 0, 30, TimeUnit.SECONDS)
    }

    private fun nearbyDevice() {
        if (btAdapter!!.isDiscovering) {
            btAdapter?.cancelDiscovery()
        }
        if (!btAdapter!!.startDiscovery()) {
            toast("Bluetooth tidak dapat dijalankan")
            Log.d(TAG, "StartDiscovery returned false. Maybe Bluetooth isn't on?")
        } else {
            Log.d(TAG, "StartDiscovery")
        }
    }
}