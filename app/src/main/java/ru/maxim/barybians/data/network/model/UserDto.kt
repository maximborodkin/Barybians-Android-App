package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val status: String?,
    @SerializedName("ubirthDate")
    val birthDate: Long,
    val sex: String,
    @SerializedName("ulastVisit")
    val lastVisit: Long,
    val roleId: Int?
)