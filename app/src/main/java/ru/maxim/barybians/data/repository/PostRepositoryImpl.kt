package ru.maxim.barybians.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.*
import ru.maxim.barybians.utils.indexOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val repositoryBound: RepositoryBound,
    private val preferencesManager: PreferencesManager
) : PostRepository {

    private val _feedPosts: MutableStateFlow<List<Post>> = MutableStateFlow(listOf())
    override val feedPosts: StateFlow<List<Post>> = _feedPosts.asStateFlow()

    override suspend fun updateFeed() {
        _feedPosts.emit(repositoryBound.wrapRequest(postService::getFeed))
    }

    override suspend fun getPostById(postId: Int): Post? =
        repositoryBound.wrapRequest { postService.getById(postId) }.firstOrNull { it.id == postId }

    override suspend fun createPost(title: String?, text: String) {
        val post = repositoryBound.wrapRequest { postService.createPost(title, text) }
        val postsList = feedPosts.value.toMutableList()
        postsList.add(0, post)
        _feedPosts.emit(postsList)
    }

    override suspend fun editPost(postId: Int, title: String?, text: String) {
        val post = repositoryBound.wrapRequest { postService.updatePost(postId, title, text) }
        val postsList = feedPosts.value.toMutableList()
        postsList.indexOrNull { it.id == postId }?.let { index ->
            postsList[index] = post
        }
        _feedPosts.emit(postsList)
    }

    override suspend fun deletePost(postId: Int) {
        val response = repositoryBound.wrapRequest { postService.deletePost(postId) }
        if (response) {
            val postsList = feedPosts.value.toMutableList()
            postsList.removeAll { it.id == postId }
            _feedPosts.emit(postsList)
        }
    }

    override suspend fun changeLike(postId: Int) {
        val hasPersonalLike = feedPosts.value.find { it.id == postId }?.likedUsers
            ?.contains { it.id == preferencesManager.userId } == true

        val likeResponse = repositoryBound.wrapRequest {
            if (hasPersonalLike) postService.removeLike(postId)
            else postService.setLike(postId)
        }

        feedPosts.value.firstOrNull { it.id == postId }?.let { post ->
            post.likedUsers = likeResponse.whoLiked
        }
        _feedPosts.emit(feedPosts.value)
    }
}