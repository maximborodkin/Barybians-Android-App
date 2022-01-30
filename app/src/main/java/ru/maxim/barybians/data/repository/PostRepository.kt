package ru.maxim.barybians.data.repository

import kotlinx.coroutines.flow.StateFlow
import ru.maxim.barybians.domain.model.Post

interface PostRepository {
    val feedPosts: StateFlow<List<Post>>
    suspend fun updateFeed()
    suspend fun getPostById(postId: Int): Post?
    suspend fun createPost(title: String?, text: String)
    suspend fun editPost(postId: Int, title: String?, text: String)
    suspend fun deletePost(postId: Int)
    suspend fun changeLike(postId: Int)
}
