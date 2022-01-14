package ru.maxim.barybians

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.maxim.barybians.di.AppComponent
import ru.maxim.barybians.di.DaggerAppComponent
import ru.maxim.barybians.di.appModule
import ru.maxim.barybians.di.dataModule
import timber.log.Timber

class App : MultiDexApplication() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent = DaggerAppComponent.builder()
            .applicationContext(applicationContext)
            .build()

        startKoin {
            androidContext(applicationContext)
            modules(listOf(
                appModule,
                dataModule
            ))
        }
    }
}