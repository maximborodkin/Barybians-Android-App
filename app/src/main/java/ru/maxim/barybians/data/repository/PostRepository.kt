package ru.maxim.barybians.data.repository

import kotlinx.coroutines.flow.StateFlow
import ru.maxim.barybians.domain.model.Post

interface PostRepository {
//    val posts: StateFlow<List<Post>>
    suspend fun loadPosts(userId: Int? = null)
    suspend fun getPostById(postId: Int): Post?
    suspend fun createPost(title: String?, text: String)
    suspend fun editPost(postId: Int, title: String?, text: String)
    suspend fun deletePost(postId: Int)
    suspend fun changeLike(postId: Int)
    suspend fun loadFeedPage(startIndex: Int, count: Int): List<Post>

    companion object {
        const val pageSize = 20
        const val prefetchDistance = 5
    }
}
