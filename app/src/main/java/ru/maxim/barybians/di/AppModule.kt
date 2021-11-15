package ru.maxim.barybians.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.maxim.barybians.utils.DateFormatUtils

val appModule = module {

    // Application scope
    factory(named("ApplicationScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

    single { DateFormatUtils(androidContext()) }
}