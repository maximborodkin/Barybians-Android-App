package ru.maxim.barybians.data.repository.post

import androidx.lifecycle.LiveData
import androidx.paging.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.database.dao.CommentDao
import ru.maxim.barybians.data.database.dao.LikeDao
import ru.maxim.barybians.data.database.dao.PostDao
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.network.model.mapper.PostDtoMapper
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.repository.RepositoryBound
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val commentDao: CommentDao,
    private val likeDao: LikeDao,
    private val postEntityMapper: PostEntityMapper,
    private val postDtoMapper: PostDtoMapper,
    private val repositoryBound: RepositoryBound,
    private val postRemoteMediatorFactory: PostRemoteMediator.PostRemoteMediatorFactory
) : PostRepository {

    override fun getFeedPager() = Pager(
        config = PagingConfig(
            initialLoadSize = PostRepository.pageSize,
            pageSize = PostRepository.pageSize,
            prefetchDistance = PostRepository.prefetchDistance,
            enablePlaceholders = true
        ),
        remoteMediator = postRemoteMediatorFactory.create(),
        pagingSourceFactory = { postDao.feedPagingSource() }
    )
        .flow
        .map { pagingData ->
            pagingData.map { entityModel ->
                postEntityMapper.toDomainModel(entityModel)
            }
        }

    override fun getUserPostsPager(userId: Int) = Pager(
        config = PagingConfig(
            initialLoadSize = PostRepository.pageSize,
            pageSize = PostRepository.pageSize,
            prefetchDistance = PostRepository.prefetchDistance,
            enablePlaceholders = true
        ),
        remoteMediator = postRemoteMediatorFactory.create(userId),
        pagingSourceFactory = { postDao.userPostsPagingSource(userId) }
    )
        .flow
        .map { pagingData ->
            pagingData.map { entityModel ->
                postEntityMapper.toDomainModel(entityModel)
            }
        }

    override fun getPostsCount(userId: Int?): Flow<Int> =
        if (userId != null && userId > 0) postDao.userPostsCount(userId)
        else postDao.feedPostsCount()

    override suspend fun createPost(uuid: String, title: String?, text: String) = withContext(IO) {
        val postDto = repositoryBound.wrapRequest { postService.createPost(uuid, title, text) }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.save(postEntityMapper.fromDomainModel(post), userDao, commentDao, likeDao)
    }

    override suspend fun editPost(postId: Int, title: String?, text: String) = withContext(IO) {
        val postDto = repositoryBound.wrapRequest { postService.updatePost(postId, title, text) }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.save(postEntityMapper.fromDomainModel(post), userDao, commentDao, likeDao)
    }

    override suspend fun deletePost(postId: Int) = withContext(IO) {
        val isDeleted = repositoryBound.wrapRequest { postService.deletePost(postId) }
        if (isDeleted) {
            postDao.delete(postId)
        }
    }
}