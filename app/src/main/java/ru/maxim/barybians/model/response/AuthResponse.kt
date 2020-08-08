package ru.maxim.barybians.model.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val user: AuthUser,
    val token: String
) {
    data class AuthUser(
        @SerializedName("userId")
        val id: Int,
        val firstName: String,
        val lastName: String,
        val photo: String?
    )
}