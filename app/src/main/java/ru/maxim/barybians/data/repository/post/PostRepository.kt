package ru.maxim.barybians.data.repository.post

import androidx.paging.PagingSource
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.domain.model.Post

interface PostRepository {
    fun feedPagingSource(): PagingSource<Int, PostEntity>
    suspend fun loadFeedPage(startIndex: Int, count: Int): List<Post>

    fun userPostsPagingSource(userId: Int): PagingSource<Int, PostEntity>
    suspend fun loadUserPostsPage(userId: Int, startIndex: Int, count: Int): List<Post>

    suspend fun createPost(title: String?, text: String)
    suspend fun editPost(postId: Int, title: String?, text: String)
    suspend fun deletePost(postId: Int)

    companion object {
        const val pageSize = 30
        const val prefetchDistance = 5
    }
}
