package ru.maxim.barybians

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.maxim.barybians.di.AppComponent
import ru.maxim.barybians.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    lateinit var appComponent: AppComponent
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

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
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("Application scope cancelled due to low memory")
    }
}