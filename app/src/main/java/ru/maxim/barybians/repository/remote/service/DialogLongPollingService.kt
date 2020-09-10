package ru.maxim.barybians.repository.remote.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.model.response.DialogResponse
import ru.maxim.barybians.repository.remote.RetrofitClient

interface DialogLongPollingService {

    @GET("/api/messages/{interlocutorId}")
    suspend fun observeMessages(@Path("interlocutorId") interlocutorId: Int,
                                @Query("lastMessage") lastMessageId: Int): Response<DialogResponse>

    companion object{
        operator fun invoke(): DialogLongPollingService =
            RetrofitClient.longPollingInstance.create(DialogLongPollingService::class.java)
    }
}