package ru.maxim.barybians.data.network.response

import ru.maxim.barybians.domain.model.User

data class LikeResponse (
    val likedUsers: ArrayList<User>,
    val likesCount: Int
)