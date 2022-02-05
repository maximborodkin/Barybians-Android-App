package ru.maxim.barybians.domain.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.domain.model.UserRole.*
import java.util.*

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
    val fullName: String
        get() = "$firstName $lastName"

    val avatarFull: String
        get() = "${RetrofitClient.AVATARS_BASE_URL}/$photo"

    val avatarMin: String
        get() = "${RetrofitClient.AVATARS_BASE_URL}/min/$photo"

    // User is online if the lastVisit less than five minutes ago
    val isOnline: Boolean
        get() = lastVisit * 1000 >= Date().time - 5 * 60 * 1000

    val role: UserRole
        get() = UserRole.values().firstOrNull { it.roleId == roleId } ?: Unverified
}