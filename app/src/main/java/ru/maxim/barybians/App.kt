package ru.maxim.barybians

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.di.AppComponent
import ru.maxim.barybians.di.DaggerAppComponent
import timber.log.Timber

class App : MultiDexApplication() {

    lateinit var appComponent: AppComponent
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        val preferencesManager = PreferencesManager(PreferenceManager.getDefaultSharedPreferences(applicationContext))

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .applicationContext(applicationContext)
            .applicationScope(applicationScope)
            .preferencesManager(preferencesManager)
            .build()

        if (preferencesManager.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        super.onCreate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("Application scope cancelled due to low memory")
    }
}