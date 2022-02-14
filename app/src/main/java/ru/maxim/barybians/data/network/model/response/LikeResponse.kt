package ru.maxim.barybians.data.network.model.response

import ru.maxim.barybians.data.network.model.UserDto

data class LikeResponse(
    val whoLiked: List<UserDto>,
    val likesCount: Int
)