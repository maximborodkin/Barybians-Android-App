package ru.maxim.barybians.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.network.RetrofitClient

val persistenceModule = module {

    single { PreferencesManager(androidContext()) }

    single { RetrofitClient(androidContext(), get()) }
    single { get<RetrofitClient>().authService }
    single { get<RetrofitClient>().userService }
    single { get<RetrofitClient>().chatService }
    single { get<RetrofitClient>().postService }
    single { get<RetrofitClient>().commentService }
}