package ru.maxim.barybians.repository.remote.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.model.User
import ru.maxim.barybians.repository.remote.RetrofitClient

interface UserService {

    @GET("/api/users/{userId}")
    suspend fun getUser(@Path("userId") UserId: Int): Response<User>

    @FormUrlEncoded
    @POST("/api/status")
    suspend fun editStatus(@Field("text") newStatus: String?): Response<String>

    companion object{
        operator fun invoke(): UserService =
            RetrofitClient.instance.create(UserService::class.java)
    }
}