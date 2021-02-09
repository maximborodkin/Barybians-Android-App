package ru.maxim.barybians

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.service.Actions
import ru.maxim.barybians.service.MessageService
import ru.maxim.barybians.utils.DateFormatUtils


class App : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        PreferencesManager.context = applicationContext
        RetrofitClient.context = applicationContext
        DateFormatUtils.context = applicationContext

//        startService(Intent(this, MessageService::class.java).apply { action = Actions.START.name })
        DateFormatUtils.currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale
        }
    }
}