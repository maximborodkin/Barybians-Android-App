package ru.maxim.barybians.domain.model

import ru.maxim.barybians.data.network.RetrofitClient
import java.util.*

data class User(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val status: String?,
    val birthDate: Date,
    val sex: String,
    val lastVisit: Date,
    val role: UserRole,
) {
    val fullName: String
        get() = "$firstName $lastName"

    val avatarFull: String
        get() = "${RetrofitClient.AVATARS_BASE_URL}/$photo"

    val avatarMin: String
        get() = "${RetrofitClient.AVATARS_BASE_URL}/min/$photo"

    // User is online if the lastVisit less than five minutes ago
    val isOnline: Boolean
        get() = lastVisit.time >= Date().time - 5 * 60 * 1000
}