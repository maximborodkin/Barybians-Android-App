package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.response.LikeResponse
import ru.maxim.barybians.domain.model.Post

interface PostRepository {
    suspend fun getFeed(): List<Post>
    suspend fun getPostById(postId: Int): Post?
    suspend fun createPost(title: String?, text: String): Post
    suspend fun updatePost(postId: Int, title: String?, text: String): Post
    suspend fun deletePost(postId: Int): Boolean
    suspend fun setLike(postId: Int): LikeResponse
    suspend fun removeLike(postId: Int): LikeResponse
}
