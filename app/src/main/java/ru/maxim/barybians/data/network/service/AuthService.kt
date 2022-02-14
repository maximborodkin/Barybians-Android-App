package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.maxim.barybians.data.network.model.response.AuthResponse
import ru.maxim.barybians.data.network.model.response.RegistrationResponse

interface AuthService {

    @FormUrlEncoded
    @POST("/v2/auth")
    suspend fun auth(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("/v2/register")
    suspend fun register(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("birthDate") birthDate: String,
        @Field("sex") sex: Boolean,
        @Field("photo") photo: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<RegistrationResponse>
}