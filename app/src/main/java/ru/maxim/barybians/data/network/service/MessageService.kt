package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.model.response.ChatResponse
import ru.maxim.barybians.data.repository.message.MessageRepositoryImpl

interface MessageService {

    @GET("/v2/messages/{user_id}?desc=true&sum=true")
    suspend fun loadMessagesPage(
        @Path("user_id") userId: Int,
        @Query("start") startIndex: Int,
        @Query("end") count: Int
    ): Response<ChatResponse>

    @FormUrlEncoded
    @POST("/v2/messages/{user_id}")
    suspend fun sendMessage(
        @Header("request") uuid: String,
        @Header("parse-mode") parseMode: String,
        @Path("user_id") userId: Int,
        @Field("text") text: String
    ): Response<MessageRepositoryImpl.SendMessageResponse>
}