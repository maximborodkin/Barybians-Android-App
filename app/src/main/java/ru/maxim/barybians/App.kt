package ru.maxim.barybians

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.maxim.barybians.di.appModule
import ru.maxim.barybians.di.persistenceModule

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(listOf(
                appModule,
                persistenceModule
            ))
        }
    }
}