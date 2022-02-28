package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.GET
import ru.maxim.barybians.data.network.model.StickerPackDto

interface StickerPackService {

    @GET("/v2/stickers")
    suspend fun getStickerPacks(): Response<List<StickerPackDto>>
}