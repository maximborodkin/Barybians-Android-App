package ru.maxim.barybians.repository.remote.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.model.User
import ru.maxim.barybians.model.response.AuthResponse
import ru.maxim.barybians.repository.remote.RetrofitClient

interface AuthService {

    @FormUrlEncoded
    @POST("/api/auth")
    suspend fun auth(@Field("username") username: String, @Field("password") password: String): Response<AuthResponse>

    @FormUrlEncoded
    @POST("/api/register")
    suspend fun register(@Field("firstName") firstName: String,
                         @Field("lastName") lastName: String,
                         @Field("birthDate") birthDate: String,
                         @Field("sex") sex: Int,
                         @Field("photo") photo: String,
                         @Field("username") username: String,
                         @Field("password") password: String): Response<JsonObject>


    companion object{
        operator fun invoke(): AuthService =
            RetrofitClient.instance.create(AuthService::class.java)
    }
}