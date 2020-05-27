package com.linkensky.ornet.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.event.PingEvent
import com.linkensky.ornet.data.model.RequestUserReport
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.MainActivity
import com.linkensky.ornet.presentation.home.BluetoothStateChanged
import com.linkensky.ornet.utils.rxApi
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import io.socket.client.Socket
import kotlinx.coroutines.GlobalScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.ext.android.inject
import org.koin.ext.scope
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScanService : BaseService() {

    private var btAdapter: BluetoothAdapter? = null
    private var scheduleTaskExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)
    private val socketClient: Socket by inject()
    private val service: SekitarKitaService by inject()

    private val btReceiver by lazy {
        BluetoothReceiver(socketClient)
    }

    companion object {
        private const val TAG = "ScanService"
    }

    override fun onStartCommand() {
        if (!Hawk.isBuilt())
            Hawk.init(applicationContext).build()

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null) {
            Toasty.error(applicationContext, "Perangkat Anda tidak memiliki Bluetooth").show()
            stopSelf()
        } else {
            if (!btAdapter!!.isEnabled) {
                EventBus.getDefault().post(BluetoothStateChanged())
            }
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(btReceiver, filter)

        startForeground()
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        if (!socketClient.connected()) socketClient.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(btReceiver)
        EventBus.getDefault().unregister(this)
        socketClient.disconnect()
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel =
                NotificationChannel(
                    Const.NOTIFICATION_SEKITAR_CHANNEL_ID,
                    Const.NOTIFICATION_SEKITAR_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationChannel.description = Const.NOTIFICATION_SEKITAR_CHANNEL_ID
            notificationChannel.setSound(null, null)

            notificationManager.createNotificationChannel(notificationChannel)

            startForeground(
                Const.NOTIFICATION_SEKITAR_FOREGROUND_ID,
                NotificationCompat.Builder(this, Const.NOTIFICATION_SEKITAR_CHANNEL_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentText("SekitarKita - sedang berjalan")
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
            Toasty.error(applicationContext, "Bluetooth tidak dapat dijalankan").show()
            Log.d(TAG, "StartDiscovery returned false. Maybe Bluetooth isn't on?")
        } else {
            Log.d(TAG, "StartDiscovery")
        }
    }

    @SuppressLint("CheckResult")
    @Subscribe
    fun onEvent(event: PingEvent) {
        Log.d(TAG, "Subs: PingEvent")
        if (socketClient.connected()) {
            socketClient.emit(
                "user-report", Gson().toJson(
                    RequestUserReport(
                        latitude = event.latitude,
                        longitude = event.longitude,
                        device_id = event.device_id,
                        area = event.area
                    )
                )
            ).on("sekitar-device-${Hawk.get<String>(Const.DEVICE_ID)}") {
                Log.d(TAG, it.first().toString())

                socketClient.off("sekitar-device-${Hawk.get<String>(Const.DEVICE_ID)}")
            }
        } else {
            Log.d(TAG, "Disconnect Socket")
        }
    }
}