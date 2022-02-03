package ru.maxim.barybians.domain.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.domain.model.UserRole.Unverified

data class User(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val status: String?,
    @SerializedName("ubirthDate")
    val birthDate: Long,
    val sex: String,
    @SerializedName("ulastVisit")
    val lastVisit: Long,
    val roleId: Int?,
) {
    val fullName: String = "$firstName $lastName"

    val avatarFull: String = "${RetrofitClient.AVATARS_BASE_URL}/$photo"

    val avatarMin: String = "${RetrofitClient.AVATARS_BASE_URL}/min/$photo"

    val role: UserRole = UserRole.values().firstOrNull { it.roleId == roleId } ?: Unverified
}