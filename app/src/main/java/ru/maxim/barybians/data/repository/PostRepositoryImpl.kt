package ru.maxim.barybians.data.repository

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.maxim.barybians.data.RepositoryBound
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.network.service.UserService
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.persistence.database.dao.PostDao
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.contains
import ru.maxim.barybians.utils.indexOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val userService: UserService,
    private val postDao: PostDao,
    private val repositoryBound: RepositoryBound,
    private val preferencesManager: PreferencesManager
) : PostRepository {

//    private val _posts: MutableStateFlow<List<Post>> = MutableStateFlow(listOf())
//    override val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    override suspend fun loadFeedPage(startIndex: Int, count: Int): List<Post> =
        repositoryBound.wrapRequest { postService.loadFeedPage(startIndex, count) }

    override fun pagingSource(): PagingSource<Int, PostEntity> {
        return postDao.pagingSource()
    }

    override suspend fun loadPosts(userId: Int?) {
//        if (userId == null) {
//            _posts.emit(repositoryBound.wrapRequest(postService::getFeed))
//        } else {
//            val user = repositoryBound.wrapRequest { userService.getUser(userId) }
//            user?.posts?.let { _posts.emit(it) }
//        }
    }

    override suspend fun getPostById(postId: Int): Post? =
        repositoryBound.wrapRequest { postService.getById(postId) }

    override suspend fun createPost(title: String?, text: String) {
        val post = repositoryBound.wrapRequest { postService.createPost(title, text) }
//        val postsList = posts.value.toMutableList()
//        postsList.add(0, post)
//        _posts.emit(postsList)
    }

    override suspend fun editPost(postId: Int, title: String?, text: String) {
        val post = repositoryBound.wrapRequest { postService.updatePost(postId, title, text) }
//        val postsList = posts.value.toMutableList()
//        postsList.indexOrNull { it.id == postId }?.let { index ->
//            postsList[index] = post
//        }
//        _posts.emit(postsList)
    }

    override suspend fun deletePost(postId: Int) {
        val response = repositoryBound.wrapRequest { postService.deletePost(postId) }
//        if (response) {
//            val postsList = posts.value.toMutableList()
//            postsList.removeAll { it.id == postId }
//            _posts.emit(postsList)
//        }
    }

    override suspend fun changeLike(postId: Int) {
//        val hasPersonalLike = posts.value.find { it.id == postId }?.likedUsers
//            ?.contains { it.id == preferencesManager.userId } == true
//
//        val likeResponse = repositoryBound.wrapRequest {
//            if (hasPersonalLike) postService.removeLike(postId)
//            else postService.setLike(postId)
//        }
//
//        posts.value.firstOrNull { it.id == postId }?.let { post ->
//            post.likedUsers = likeResponse.whoLiked
//        }
//        _posts.emit(posts.value)
    }
}