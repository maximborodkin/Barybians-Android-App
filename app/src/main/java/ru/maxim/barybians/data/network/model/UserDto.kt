package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName
import ru.maxim.barybians.domain.model.UserRole

data class UserDto(
    @SerializedName("id")
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val status: String?,
    @SerializedName("ubirthDate")
    val birthDate: Long,
    @SerializedName("sex")
    val gender: Int,
    @SerializedName("ulastVisit")
    val lastVisit: Long,
    val roleId: Int? = UserRole.Unverified.roleId
)