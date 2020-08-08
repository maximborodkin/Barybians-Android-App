package ru.maxim.barybians

import android.app.Application
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.RetrofitClient

class App : Application() {

    override fun onCreate() {
        PreferencesManager.context = applicationContext
        RetrofitClient.context = applicationContext
        super.onCreate()
    }
}