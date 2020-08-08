package ru.maxim.barybians.model.response

import ru.maxim.barybians.model.User

data class LikeResponse (
    val likedUsers: ArrayList<User>,
    val likesCount: Int
)