package ru.maxim.barybians.domain.model

import ru.maxim.barybians.data.network.NetworkManager
import java.util.*

data class User(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val status: String?,
    val birthDate: Date,
    val gender: Gender,
    val lastVisit: Date,
    val role: UserRole,
) {
    val fullName: String
        get() = "$firstName $lastName"

    val avatarFull: String
        get() = "${NetworkManager.AVATARS_BASE_URL}/$photo"

    val avatarMin: String
        get() = "${NetworkManager.AVATARS_BASE_URL}/min/$photo"

    // User is online if the lastVisit less than five minutes ago
    val isOnline: Boolean
        get() = lastVisit.time >= Date().time - 5 * 60 * 1000
}