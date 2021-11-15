package ru.maxim.barybians.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.remote.RetrofitClient

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
        get() = "$firstName $lastName"

    fun getAvatarUrl(loadFull: Boolean = false) =
        if (photo != null) "${RetrofitClient.BASE_URL}/avatars${if (loadFull) "" else "/min"}/$photo"
        else null

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
}