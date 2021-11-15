package ru.maxim.barybians

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.maxim.barybians.di.appModule
import ru.maxim.barybians.di.persistenceModule
import timber.log.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(applicationContext)
            modules(listOf(
                appModule,
                persistenceModule
            ))
        }
    }
}