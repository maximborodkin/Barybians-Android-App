package ru.maxim.barybians.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import ru.maxim.barybians.repository.local.PreferencesManager

class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
//            Intent(context, MessageService::class.java).also {
//                it.action = Actions.START.name
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(it)
//                    return
//                }
//                context.startService(it)
//            }
//        }
    }
}