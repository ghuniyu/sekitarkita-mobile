package com.linkensky.ornet.service

import android.app.Service
import android.content.Intent

abstract class BaseService : Service() {
    private var isRunning = false

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true

            onStartCommand()
        }

        return START_STICKY
    }

    protected abstract fun onStartCommand()
}