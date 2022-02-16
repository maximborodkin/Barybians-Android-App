package ru.maxim.barybians.data.repository

import androidx.paging.PagingSource
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.LikeDao
import ru.maxim.barybians.data.database.dao.PostDao
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.network.model.mapper.PostDtoMapper
import ru.maxim.barybians.data.network.service.PostService
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
    private val postDtoMapper: PostDtoMapper,
    private val repositoryBound: RepositoryBound,
    private val preferencesManager: PreferencesManager
) : PostRepository {

    override suspend fun loadFeedPage(startIndex: Int, count: Int): List<Post> {
        val postDto = repositoryBound.wrapRequest { postService.loadFeedPage(startIndex, count) }
        return postDtoMapper.toDomainModelList(postDto)
    }

    override fun feedPagingSource(): PagingSource<Int, PostEntity> {
        return postDao.feedPagingSource()
    }

    override suspend fun getPostById(postId: Int): Post? {
        val postDto = repositoryBound.wrapRequest { postService.getById(postId) } ?: return null
        return postDtoMapper.toDomainModel(postDto)
    }

    override suspend fun createPost(title: String?, text: String) {
        val postDto = repositoryBound.wrapRequest { postService.createPost(title, text) }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.insert(postEntityMapper.fromDomainModel(post).post)
    }

    override suspend fun editPost(postId: Int, title: String?, text: String) {
        val postDto = repositoryBound.wrapRequest { postService.updatePost(postId, title, text) }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.insert(postEntityMapper.fromDomainModel(post).post)
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

        val likeEntities = likeResponse.whoLiked.map { user -> LikeEntity(postId = postId, userId = user.userId) }
        likeDao.insert(likeEntities)
    }
}