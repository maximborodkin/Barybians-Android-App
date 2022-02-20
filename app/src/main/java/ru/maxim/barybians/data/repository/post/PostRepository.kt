package ru.maxim.barybians.data.repository.post

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User

interface PostRepository {
    fun feedPagingSource(): PagingSource<Int, PostEntity>
    suspend fun loadFeedPage(startIndex: Int, count: Int): List<Post>
    suspend fun getPostById(postId: Int): Post?
    suspend fun createPost(title: String?, text: String)
    suspend fun editPost(postId: Int, title: String?, text: String)
    suspend fun deletePost(postId: Int)

    companion object {
        const val pageSize = 30
        const val prefetchDistance = 5
    }
}
