package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.model.UserDto

interface UserService {

    @GET("/v2/users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): Response<UserDto?>

    /**
     * @return updated status*/
    @FormUrlEncoded
    @POST("/v2/status")
    suspend fun editStatus(@Field("text") newStatus: String): Response<String>

    @GET("/v2/users")
    suspend fun getAll(): Response<List<UserDto>>
}