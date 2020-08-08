package ru.maxim.barybians.repository.remote.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import ru.maxim.barybians.repository.remote.RetrofitClient

interface DialogService {

    @GET("/api/dialogs")
    suspend fun getDialogsList(): Response<JsonObject>

    companion object{
        operator fun invoke(): DialogService =
            RetrofitClient.instance.create(DialogService::class.java)
    }
}