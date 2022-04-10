package ru.maxim.barybians.data.network.model.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val user: AuthUser?,
    val token: String?
) {
    data class AuthUser(
        val userId: Int,
        val firstName: String,
        val lastName: String,
        val photo: String?,
        val status: String?,
        @SerializedName("ubirthDate")
        val birthDate: Long,
        val sex: Int,
        @SerializedName("ulastVisit")
        val lastVisit: Long,
        val role: Int? = 0 //TODO: remove nullability when the network response will contain this field
    )
}