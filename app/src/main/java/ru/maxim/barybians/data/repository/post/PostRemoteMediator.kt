package ru.maxim.barybians.data.repository.post

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import dagger.Reusable
import ru.maxim.barybians.data.database.BarybiansDatabase
import ru.maxim.barybians.data.database.dao.CommentDao
import ru.maxim.barybians.data.database.dao.LikeDao
import ru.maxim.barybians.data.database.dao.PostDao
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.network.model.mapper.PostDtoMapper
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.utils.transform
import timber.log.Timber
import javax.inject.Inject

@Reusable
@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator private constructor(
    private val userId: Int?,
    private val database: BarybiansDatabase,
    private val repositoryBound: RepositoryBound,
    private val postEntityMapper: PostEntityMapper,
    private val postDtoMapper: PostDtoMapper,
    private val postService: PostService,
    private val postDao: PostDao,
    private val likeDao: LikeDao,
    private val userDao: UserDao,
    private val commentDao: CommentDao
) : RemoteMediator<Int, PostEntity>() {

    /*
    * My heaven - the eye of the storm
    * The place I want to go
    * Serene and beautiful
    * My place to rest
    * My heart and soul
    * */
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        return try {
            val page: Int = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val last = state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = false)
                    last.post.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val startIndex = page * state.config.pageSize
            val count = state.config.pageSize
            val feedPageResponse = if (userId == null || userId <= 0) {
                val postDto = repositoryBound.wrapRequest { postService.loadFeedPage(startIndex = startIndex, count = count) }
                postDtoMapper.toDomainModelList(postDto)
            } else {
                val postDto = repositoryBound.wrapRequest {
                    postService.loadUserPostsPage(userId = userId, startIndex = startIndex, count = count)
                }
                postDtoMapper.toDomainModelList(postDto)
            }

            val prevPage = if (page == 0) null else page - 1
            val nextPage = if (feedPageResponse.size < state.config.pageSize) null else page + 1

            val entities = postEntityMapper.fromDomainModelList(feedPageResponse).transform { post ->
                post.post.prevPage = prevPage; post.post.nextPage = nextPage
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
//                    postDao.delete()
                }
                postDao.save(entities, userDao, commentDao, likeDao)
            }
            MediatorResult.Success(endOfPaginationReached = feedPageResponse.size < state.config.pageSize)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }

    class PostRemoteMediatorFactory @Inject constructor(
        private val database: BarybiansDatabase,
        private val repositoryBound: RepositoryBound,
        private val postEntityMapper: PostEntityMapper,
        private val postDtoMapper: PostDtoMapper,
        private val postService: PostService,
        private val postDao: PostDao,
        private val likeDao: LikeDao,
        private val userDao: UserDao,
        private val commentDao: CommentDao
    ) {
        fun create(userId: Int? = null) = PostRemoteMediator(
            userId = userId,
            database = database,
            postEntityMapper = postEntityMapper,
            postDtoMapper = postDtoMapper,
            repositoryBound = repositoryBound,
            postService = postService,
            postDao = postDao,
            likeDao = likeDao,
            userDao = userDao,
            commentDao = commentDao
        )
    }
}