package ru.maxim.barybians.data.network.service

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.model.response.AuthResponse
import ru.maxim.barybians.data.network.model.response.RegistrationResponse

interface AuthService {

    @FormUrlEncoded
    @POST("/v2/auth")
    suspend fun auth(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @Multipart
    @POST("/v2/register")
    suspend fun register(
        @Part firstName: MultipartBody.Part,
        @Part lastName: MultipartBody.Part,
        @Part birthDate: MultipartBody.Part,
        @Part gender: MultipartBody.Part,
        @Part photo: MultipartBody.Part?,
        @Part username: MultipartBody.Part,
        @Part password: MultipartBody.Part
    ): Response<RegistrationResponse>
}