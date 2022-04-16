package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.GET
import ru.maxim.barybians.data.network.model.ChatDto

interface ChatService {

    @GET("/v2/dialogs")
    suspend fun getChatsList(): Response<List<ChatDto>>
}