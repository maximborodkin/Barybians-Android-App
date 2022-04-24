package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.maxim.barybians.data.network.model.ChatDto
import ru.maxim.barybians.data.network.model.response.ChatResponse

interface MessageService {

    @GET("/v2/messages/{user_id}?desc=true&sum=true")
    suspend fun loadMessagesPage(
        @Path("user_id") userId: Int,
        @Query("start") startIndex: Int,
        @Query("end") count: Int
    ): Response<ChatResponse>
}