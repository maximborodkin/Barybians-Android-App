package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.domain.model.User

interface UserService {

    @GET("/api/users/{userId}")
    suspend fun getUser(@Path("userId") UserId: Int): Response<User>

    @FormUrlEncoded
    @POST("/api/status")
    suspend fun editStatus(@Field("text") newStatus: String?): Response<String>
}