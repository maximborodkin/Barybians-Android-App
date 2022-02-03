package ru.maxim.barybians.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.maxim.barybians.R

enum class UserRole(
    val roleId: Int,
    @StringRes val stringResource: Int,
    @DrawableRes val iconResource: Int
) {
    Administrator(1, R.string.administrator, R.drawable.ic_role_administrator),
    Barybian(2, R.string.barybian, R.drawable.ic_role_barybian),
    Verified(3, R.string.verified, R.drawable.ic_role_verified),
    Unverified(4, R.string.unverified, 0)
}