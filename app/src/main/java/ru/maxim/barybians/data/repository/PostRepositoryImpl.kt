package ru.maxim.barybians.data.repository

import androidx.paging.PagingSource
import ru.maxim.barybians.data.RepositoryBound
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.LikeDao
import ru.maxim.barybians.data.database.dao.PostDao
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.isNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao,
    private val likeDao: LikeDao,
    private val postEntityMapper: PostEntityMapper,
    private val repositoryBound: RepositoryBound,
    private val preferencesManager: PreferencesManager
) : PostRepository {

    override suspend fun loadFeedPage(startIndex: Int, count: Int): List<Post> =
        repositoryBound.wrapRequest { postService.loadFeedPage(startIndex, count) }

    override fun feedPagingSource(): PagingSource<Int, PostEntity> {
        return postDao.pagingSource()
    }

    override suspend fun getPostById(postId: Int): Post? =
        repositoryBound.wrapRequest { postService.getById(postId) }

    override suspend fun createPost(title: String?, text: String) {
        val post = repositoryBound.wrapRequest { postService.createPost(title, text) }
        postDao.insert(postEntityMapper.fromDomainModel(post))
    }

    override suspend fun editPost(postId: Int, title: String?, text: String) {
        val post = repositoryBound.wrapRequest { postService.updatePost(postId, title, text) }
        postDao.insert(postEntityMapper.fromDomainModel(post))
    }

    override suspend fun deletePost(postId: Int) {
        val isDeleted = repositoryBound.wrapRequest { postService.deletePost(postId) }
        if (isDeleted) {
            postDao.delete(postId)
        }
    }

    override suspend fun changeLike(postId: Int) {
        val hasPersonalLike = likeDao.getLike(postId, preferencesManager.userId).isNotNull()

        val likeResponse = repositoryBound.wrapRequest {
            if (hasPersonalLike) postService.removeLike(postId)
            else postService.setLike(postId)
        }

        val likeEntities = likeResponse.whoLiked.map { user -> LikeEntity(postId = postId, userId = user.id) }
        likeDao.insert(likeEntities)
    }
}