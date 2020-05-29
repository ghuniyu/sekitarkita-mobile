package com.linkensky.ornet.service

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
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.event.BluetoothStateChanged
import com.linkensky.ornet.data.event.PingEvent
import com.linkensky.ornet.data.event.ZoneEvent
import com.linkensky.ornet.data.model.Address
import com.linkensky.ornet.data.model.StoreLocationRequest
import com.linkensky.ornet.data.model.StoreLocationResponse
import com.linkensky.ornet.data.services.SekitarKitaService
import com.linkensky.ornet.presentation.MainActivity
import com.linkensky.ornet.utils.fromJson
import com.linkensky.ornet.utils.rxApi
import com.linkensky.ornet.utils.toJson
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.subscribeBy
import io.socket.client.Socket
import kotlinx.coroutines.GlobalScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.ext.android.inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScanService : BaseService() {

    private var btAdapter: BluetoothAdapter? = null
    private var scheduleTaskExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)
    private val socketClient: Socket by inject()
    private val service: SekitarKitaService by inject()

    private val btReceiver by lazy {
        BluetoothReceiver(service)
    }

    companion object {
        private const val TAG = "ScanService"
    }

    override fun onStartCommand() {
        if (!Hawk.isBuilt())
            Hawk.init(applicationContext).build()

        if (!socketClient.connected()) socketClient.connect()

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

    @Subscribe
    fun onEvent(event: PingEvent) {
        Log.d(TAG, "Subs: PingEvent")
        val lastKnownLatitude = Hawk.get<Double>(Const.STORAGE_LASTKNOWN_LAT)
        val lastKnownLongitude = Hawk.get<Double>(Const.STORAGE_LASTKNOWN_LNG)
        val deviceId = Hawk.get<String>(Const.DEVICE_ID)
        val area = Hawk.get<Address?>(Const.STORAGE_LASTKNOWN_ADDRESS)

        val request = StoreLocationRequest(
            latitude = lastKnownLatitude,
            longitude = lastKnownLongitude,
            device_id = deviceId,
            area = "${area?.village}, ${area?.district}, ${area?.city}",
            address = area.toString()
        )
        if (socketClient.connected()) {
            socketClient.emit(
                Const.EVENT_USER_REPORT,
                request.toJson()
            ).on(Const.EVENT_USER_DEVICE.format(Hawk.get<String>(Const.DEVICE_ID))) {
                Log.d(TAG, it.first().toString())
                val response = it.first().toString().fromJson<StoreLocationResponse>()
                response.zone?.let { zone ->
                    Hawk.put(Const.STORAGE_LASTKNOWN_ZONE, getZoneChar(zone))
                    EventBus.getDefault().post(ZoneEvent())
                }

                socketClient.off(Const.EVENT_USER_DEVICE.format(Hawk.get<String>(Const.DEVICE_ID)))
            }
        } else {
            Log.d(TAG, "Disconnect Socket")
            GlobalScope.rxApi {
                service.postStoreLocation(request)
            }.subscribeBy(onSuccess = { response ->
                response.zone?.let {
                    Hawk.put(Const.STORAGE_LASTKNOWN_ZONE, getZoneChar(it))
                    EventBus.getDefault().post(ZoneEvent())
                }
            },
                onError = { Log.d(TAG, it.message.toString()) })
        }
    }

    private fun getZoneChar(zone: String): Char {
        val zoneChar = when (zone) {
            "merah" -> 'r'
            "hijau" -> 'g'
            else -> 'y'
        }
        if (zoneChar == 'r') {
            redZoneNotification()
        }

        return zoneChar
    }

    private fun redZoneNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder =
            NotificationCompat.Builder(applicationContext, Const.NOTIFICATION_SCORE_DEFAULT)
        builder.setContentTitle("SekitarKita Alert!")
        builder.setContentText("Anda Berada di Kawasan Zona Merah")
        builder.setChannelId(Const.NOTIFICATION_ZONE_CHANNEL_ID)
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        val notification = builder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Const.NOTIFICATION_ZONE_CHANNEL_ID,
                Const.NOTIFICATION_ZONE_CHANNEL,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(Const.NOTIFICATION_ZONE_ID_VALUE, notification)
    }
}