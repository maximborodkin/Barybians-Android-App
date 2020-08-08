package ru.maxim.barybians

import android.app.Application
import android.os.Build
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.ui.DialogFactory
import ru.maxim.barybians.utils.DateFormatUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesManager.context = applicationContext
        RetrofitClient.context = applicationContext

        DateFormatUtils.currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale
        }
    }
}