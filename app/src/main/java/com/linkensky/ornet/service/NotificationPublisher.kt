package com.linkensky.ornet.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.linkensky.ornet.utils.Constant


class NotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NotificationPublisher", "Notification Requested")
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, Constant.NOTIFICATION_SCORE_DEFAULT)
        builder.setContentTitle("Score anda sudah siap")
        builder.setContentText("Anda adalah Penjuang...")
        builder.setChannelId(Constant.NOTIFICATION_SCORE_CHANNEL_ID)
        val notification = builder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constant.NOTIFICATION_SCORE_CHANNEL_ID,
                Constant.NOTIFICATION_SCORE_CHANNEL,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(Constant.NOTIFICATION_SCORE_ID_VALUE, notification)
    }

}