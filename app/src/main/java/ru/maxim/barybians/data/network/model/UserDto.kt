package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val status: String?,
    @SerializedName("ubirthDate")
    val _birthDate: Long,
    val sex: String,
    @SerializedName("ulastVisit")
    val _lastVisit: Long,
    val roleId: Int?
)