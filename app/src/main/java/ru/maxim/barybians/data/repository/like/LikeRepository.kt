package ru.maxim.barybians.data.repository.like

import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.domain.model.User

interface LikeRepository {
    fun getLikes(postId: Int): Flow<List<User>>
    suspend fun changeLike(postId: Int)
}