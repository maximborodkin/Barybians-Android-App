package ru.maxim.barybians.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.maxim.barybians.data.persistence.database.BarybiansDatabase
import ru.maxim.barybians.data.persistence.database.dao.PostDao
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.utils.transform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator @Inject constructor(
    private val feedRepository: PostRepository,
    private val database: BarybiansDatabase,
    private val postEntityMapper: PostEntityMapper,
    private val postDao: PostDao
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
                    last.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val feedPageResponse = feedRepository.loadFeedPage(
                startIndex = page * state.config.pageSize,
                count = state.config.pageSize
            )

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (feedPageResponse.size < state.config.pageSize) null else page + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.clearAll()
                }

                val entities = postEntityMapper.fromDomainModelList(feedPageResponse)
                    .transform { post -> post.page = page; post.prevKey = prevKey; post.nextKey = nextKey }

                postDao.insertAll(entities)
            }
            MediatorResult.Success(endOfPaginationReached = feedPageResponse.size < state.config.pageSize)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}