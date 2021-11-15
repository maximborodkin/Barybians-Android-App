package ru.maxim.barybians.di

import org.koin.dsl.bind
import org.koin.dsl.module
import ru.maxim.barybians.data.repository.AuthRepository
import ru.maxim.barybians.data.repository.AuthRepositoryImpl

val dataModule = module {

    single { AuthRepositoryImpl(get(), get(), get()) } bind AuthRepository::class
}