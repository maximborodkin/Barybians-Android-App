package ru.maxim.barybians.data.network

import dagger.Reusable

@Reusable
object NetworkManager {

    const val REST_BASE_URL = "https://api.barybians.ru"
    const val WS_BASE_URL = "wss://barybians.ru:3000"
    const val STICKERS_BASE_URL = "https://content.barybians.ru/stickers"
    const val AVATARS_BASE_URL = "https://content.barybians.ru/avatars"
    const val DEFAULT_AVATAR_URL = "https://content.barybians.ru/avatars/j.png"
}