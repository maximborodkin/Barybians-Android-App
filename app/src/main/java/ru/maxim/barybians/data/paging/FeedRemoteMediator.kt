package ru.maxim.barybians.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import dagger.Reusable
import ru.maxim.barybians.data.database.BarybiansDatabase
import ru.maxim.barybians.data.database.dao.CommentDao
import ru.maxim.barybians.data.database.dao.PostDao
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.utils.transform
import timber.log.Timber
import javax.inject.Inject

@Reusable
@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator @Inject constructor(
    private val feedRepository: PostRepository,
    private val database: BarybiansDatabase,
    private val postEntityMapper: PostEntityMapper,
    private val postDao: PostDao,
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
        Timber.d("XXX $loadType nextPage: ${state.lastItemOrNull()?.post?.nextPage}")
        return try {
            val page: Int = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val last = state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = false)
                    last.post.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val feedPageResponse = feedRepository.loadFeedPage(
                startIndex = page * state.config.pageSize,
                count = state.config.pageSize
            )
            Timber.d("XXX loaded ${feedPageResponse.size} first:${feedPageResponse.first().postId}, last:${feedPageResponse.last().postId}")

            val prevPage = if (page == 0) null else page - 1
            val nextPage = if (feedPageResponse.size < state.config.pageSize) null else page + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.delete()
                }

                val entities = postEntityMapper.fromDomainModelList(feedPageResponse)
                    .transform { post -> post.post.prevPage = prevPage; post.post.nextPage = nextPage }

                postDao.savePosts(userDao, commentDao, entities)
                Timber.d("XXX Saved posts: ${postDao.getPostsCount()}, users: ${postDao.getUsersCount()}, comments: ${postDao.getCommentsCount()}, likes: ${postDao.getLikesCount()}")
            }
            MediatorResult.Success(endOfPaginationReached = feedPageResponse.size < state.config.pageSize)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }
}