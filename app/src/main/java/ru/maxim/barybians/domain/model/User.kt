package ru.maxim.barybians.domain.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.domain.model.UserRole.Unverified
import java.util.*

data class User(
    @PrimaryKey(autoGenerate = false)
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
    val roleId: Int?,
) {
    val fullName: String
        get() = "$firstName $lastName"

    val avatarFull: String
        get() = "${RetrofitClient.AVATARS_BASE_URL}/$photo"

    val avatarMin: String
        get() = "${RetrofitClient.AVATARS_BASE_URL}/min/$photo"

    val birthDate: Long
        get() = _birthDate * 1000

    val lastVisit: Long
        get() = _lastVisit * 1000

    // User is online if the lastVisit less than five minutes ago
    val isOnline: Boolean
        get() = lastVisit >= Date().time - 5 * 60 * 1000

    val role: UserRole
        get() = UserRole.values().firstOrNull { it.roleId == roleId } ?: Unverified
}