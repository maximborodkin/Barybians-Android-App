package ru.maxim.barybians.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.repository.AuthRepository
import ru.maxim.barybians.data.repository.AuthRepositoryImpl

val dataModule = module {
// Network
    single { RetrofitClient(androidContext(), get()) }
    single { get<RetrofitClient>().authService }
    single { get<RetrofitClient>().userService }
    single { get<RetrofitClient>().chatService }
    single { get<RetrofitClient>().postService }
    single { get<RetrofitClient>().commentService }

// Repository
    single { AuthRepositoryImpl(get(), get(), get()) } bind AuthRepository::class

// Persistence
    single { PreferencesManager(androidContext()) }
}