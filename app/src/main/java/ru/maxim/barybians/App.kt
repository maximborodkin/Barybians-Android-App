package ru.maxim.barybians

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.maxim.barybians.di.AppComponent
import ru.maxim.barybians.di.DaggerAppComponent
import ru.maxim.barybians.service.MessageService
import timber.log.Timber

class App : MultiDexApplication() {

    lateinit var appComponent: AppComponent
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .applicationContext(applicationContext)
            .applicationScope(applicationScope)
            .build()

        if (appComponent.preferencesManager.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        super.onCreate()

//        val messageServiceIntent = Intent(this, MessageService::class.java)
//        startService(messageServiceIntent)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("Application scope cancelled due to low memory")
    }
}