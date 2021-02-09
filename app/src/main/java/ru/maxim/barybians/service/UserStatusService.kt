package ru.maxim.barybians.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class UserStatusService : Service() {

    private val binder = StatusServiceBinder()
    private val statuses = HashMap<Int, Boolean>() // <User id, is online>

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    inner class StatusServiceBinder : Binder() {
        fun getService() = this@UserStatusService
    }
}