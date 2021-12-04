package ru.maxim.barybians.data.repository

import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.domain.model.Post

interface PostRepository {
    fun getFeed(): Flow<Result<List<Post>>>
    fun createPost(): Flow<Result<Unit>>
    fun updatePost(): Flow<Result<Unit>>
    fun deletePost(): Flow<Result<Unit>>
    fun setLike(): Flow<Result<Unit>>
    fun removeLike(): Flow<Result<Unit>>
}
