package ru.maxim.barybians

import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.maxim.barybians.di.AppComponent
import ru.maxim.barybians.di.DaggerAppComponent
import timber.log.Timber

class App : MultiDexApplication() {

    lateinit var appComponent: AppComponent
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .applicationContext(applicationContext)
            .applicationScope(applicationScope)
            .build()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("Application scope cancelled due to low memory")
    }
}