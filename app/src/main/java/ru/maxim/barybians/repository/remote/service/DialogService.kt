package ru.maxim.barybians.repository.remote.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.model.response.DialogResponse
import ru.maxim.barybians.repository.remote.RetrofitClient

interface DialogService {

    @GET("/api/dialogs")
    suspend fun getDialogsList(): Response<JsonObject>

    @GET("/api/dialogs/{userId}")
    suspend fun getMessages(@Path("userId") userId: Int): Response<DialogResponse>

    @FormUrlEncoded
    @POST("/api/dialogs/{userId}")
    suspend fun sendMessage(@Path("userId") userId: Int, @Field("text") text: String): Response<String>

    @GET("/api/messages/{interlocutorId}/lastMessage={lastMessageId}")
    suspend fun observeMessagesForUser(@Path("interlocutorId") interlocutorId: Int,
                                       @Path("lastMessageId") lastMessageId: Int): Response<DialogResponse>

    @GET("/api/messages/lastMessage={lastMessageId}")
    suspend fun observeNewMessages(@Path("lastMessageId") lastMessageId: Int): Response<DialogResponse>

    companion object {
        operator fun invoke(): DialogService =
            RetrofitClient.instance.create(DialogService::class.java)
    }
}