package ru.maxim.barybians.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.maxim.barybians.R

enum class UserRole(
    val roleId: Int,
    @StringRes val stringResource: Int,
    @DrawableRes val iconResource: Int?
) {
    Administrator(roleId = 1, stringResource = R.string.administrator, iconResource = R.drawable.ic_role_administrator),
    Barybian(2, stringResource = R.string.barybian, iconResource = R.drawable.ic_role_barybian),
    Verified(3, stringResource = R.string.verified, iconResource = R.drawable.ic_role_verified),
    Unverified(4, stringResource = R.string.unverified, iconResource = null)
}