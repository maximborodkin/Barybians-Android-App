package ru.maxim.barybians.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.RetrofitClient

@Entity
data class User (
    @PrimaryKey
    val id: Int,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val status: String?,
    @SerializedName("ubirthDate")
    val birthDate: Long,
    private val sex: String,
    @SerializedName("ulastVisit")
    val lastVisit: Long,
    val roleId: Int?,
    val posts: ArrayList<Post>
) {
    val fullName: String
        get() = getFullName(firstName, lastName)

    val avatarFull: String
        get() = getAvatarFull(photo)

    val avatarMin: String
        get() = getAvatarMin(photo)

    fun getRole() = when(roleId) {
        Role.Administrator.roleId -> Role.Administrator
        Role.Barybian.roleId -> Role.Barybian
        Role.Verified.roleId -> Role.Verified
        else -> Role.Unverified
    }

    enum class Role(val roleId: Int, val stringResource: Int, val iconResource: Int) {
        Administrator(1, R.string.administrator, R.drawable.ic_role_administrator),
        Barybian(2, R.string.barybian, R.drawable.ic_role_barybian),
        Verified(3, R.string.verified, R.drawable.ic_role_verified),
        Unverified(4, R.string.unverified, 0)
    }

    companion object {
        fun getFullName(firstName: String, lastName: String) = "$firstName $lastName"
        fun getAvatarFull(photoName: String?) = "${RetrofitClient.AVATARS_BASE_URL}/$photoName"
        fun getAvatarMin(photoName: String?) = "${RetrofitClient.AVATARS_BASE_URL}/min/$photoName"
    }
}