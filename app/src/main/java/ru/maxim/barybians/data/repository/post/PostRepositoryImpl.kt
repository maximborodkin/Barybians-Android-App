package ru.maxim.barybians.data.repository.post

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.database.dao.*
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.network.model.ParseMode
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
    private val attachmentDao: AttachmentDao,
    private val postAttachmentDao: PostAttachmentDao,
    private val commentAttachmentDao: CommentAttachmentDao,
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
        .map { pagingData -> pagingData.map { entityModel -> postEntityMapper.toDomainModel(entityModel) } }

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
        .map { pagingData -> pagingData.map { entityModel -> postEntityMapper.toDomainModel(entityModel) } }

    override fun getPostsCount(userId: Int?): Flow<Int> =
        if (userId != null && userId > 0) postDao.userPostsCount(userId)
        else postDao.feedPostsCount()

    override suspend fun createPost(uuid: String, title: String?, text: String, parseMode: ParseMode) = withContext(IO) {
        val postDto = repositoryBound.wrapRequest {
            postService.createPost(
                parseMode = parseMode.headerValue,
                uuid = uuid,
                title = title,
                text = text
            )
        }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.save(
            postEntityMapper.fromDomainModel(post),
            attachmentDao,
            postAttachmentDao,
            commentAttachmentDao,
            userDao,
            commentDao,
            likeDao
        )
    }

    override suspend fun editPost(postId: Int, title: String?, text: String, parseMode: ParseMode) = withContext(IO) {
        val postDto = repositoryBound.wrapRequest {
            postService.updatePost(
                parseMode = parseMode.headerValue,
                postId = postId,
                title = title,
                text = text
            )
        }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.save(
            postEntityMapper.fromDomainModel(post),
            attachmentDao,
            postAttachmentDao,
            commentAttachmentDao,
            userDao,
            commentDao,
            likeDao
        )
    }

    override suspend fun deletePost(postId: Int) = withContext(IO) {
        val isDeleted = repositoryBound.wrapRequest { postService.deletePost(postId) }
        if (isDeleted) {
            postDao.clear(postId)
        }
    }
}