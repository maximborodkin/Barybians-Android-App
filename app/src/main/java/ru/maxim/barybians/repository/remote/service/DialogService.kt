package ru.maxim.barybians.repository.remote.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.maxim.barybians.model.Message
import ru.maxim.barybians.model.response.DialogResponse
import ru.maxim.barybians.repository.remote.RetrofitClient

interface DialogService {

    @GET("/api/dialogs")
    suspend fun getDialogsList(): Response<JsonObject>

    @GET("/api/dialogs/{userId}")
    suspend fun getMessages(@Path("userId") userId: Int): Response<DialogResponse>

    companion object{
        operator fun invoke(): DialogService =
            RetrofitClient.instance.create(DialogService::class.java)
    }
}