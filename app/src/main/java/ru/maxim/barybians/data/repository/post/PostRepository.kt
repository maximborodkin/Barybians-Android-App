package ru.maxim.barybians.data.repository.post

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.domain.model.Post

interface PostRepository {
    fun getFeedPager(): Flow<PagingData<Post>>
    fun getUserPostsPager(userId: Int): Flow<PagingData<Post>>
    fun getPostsCount(userId: Int? = null): Flow<Int>
    suspend fun createPost(uuid: String, title: String?, text: String)
    suspend fun editPost(postId: Int, title: String?, text: String)
    suspend fun deletePost(postId: Int)

    companion object {
        const val pageSize = 30
        const val prefetchDistance = 5
    }

}
