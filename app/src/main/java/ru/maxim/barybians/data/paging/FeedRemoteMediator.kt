package ru.maxim.barybians.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.maxim.barybians.data.persistence.database.BarybiansDatabase
import ru.maxim.barybians.data.persistence.database.dao.PostDao
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.PostPagingKey
import ru.maxim.barybians.data.persistence.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.repository.PostRepository
import timber.log.Timber
import java.io.InvalidObjectException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator @Inject constructor(
    private val feedRepository: PostRepository,
    private val database: BarybiansDatabase,
    private val postEntityMapper: PostEntityMapper,
    private val postDao: PostDao
) : RemoteMediator<Int, PostEntity>() {
    val tag = "XXX FeedRemoteMediator"

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
                    val lastItem = state.lastItemOrNull() ?: throw InvalidObjectException("Result is empty")
                    val nextKey = database.withTransaction { postDao.keyByPostId(lastItem.postId) }
                    nextKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            Timber.tag(tag).d("load $loadType page $page")

            val feedPageResponse = feedRepository.loadFeedPage(
                startIndex = page * state.config.pageSize,
                count = state.config.pageSize
            )

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (feedPageResponse.size < state.config.pageSize) null else page + 1
            val keys = feedPageResponse.map { post -> PostPagingKey(post.id, prevKey = prevKey, nextKey = nextKey) }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.clearAll()
                    postDao.clearKeys()
                }

                postDao.insertAll(postEntityMapper.fromDomainModelList(feedPageResponse))
                postDao.insertKeys(keys)
            }

            return MediatorResult.Success(endOfPaginationReached = feedPageResponse.size < state.config.pageSize)
        } catch (e: Exception) {
            Timber.tag(tag).w(e)
            MediatorResult.Error(e)
        }
    }
}