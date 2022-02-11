package ru.maxim.barybians.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import ru.maxim.barybians.data.persistence.database.BarybiansDatabase
import ru.maxim.barybians.data.persistence.database.dao.PostDao
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.utils.transform
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator @Inject constructor(
    private val feedRepository: PostRepository,
    private val database: BarybiansDatabase,
    private val postEntityMapper: PostEntityMapper,
    private val postDao: PostDao
) : RemoteMediator<Int, PostEntity>() {
    private var prevKey: Int? = null
    private var nextKey: Int? = null
    val tag = "XXX FeedRemoteMediator"

    /*
    * My heaven - the eye of the storm
    * The place I want to go
    * Serene and beautiful
    * My place to rest
    * My heart and soul
    * */
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        Timber.tag(tag)
            .d("${currentCoroutineContext().hashCode()} load $loadType last: ${state.lastItemOrNull()?.postId}")

        return try {
            val page: Int = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
//                    val last = state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = false)
//                    last.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            Timber.tag(tag).d("${currentCoroutineContext().hashCode()} load $loadType page $page")

            val feedPageResponse = feedRepository.loadFeedPage(
                startIndex = page * state.config.pageSize,
                count = state.config.pageSize
            )
            Timber.tag(tag)
                .d("${currentCoroutineContext().hashCode()} loaded page $page with size ${feedPageResponse.size}")

            prevKey = if (page == 0) null else page - 1
            nextKey = if (feedPageResponse.size < state.config.pageSize) null else page + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.clearAll()
                }

                val entities = postEntityMapper.fromDomainModelList(feedPageResponse)
                    .transform { post -> post.prevKey = prevKey; post.nextKey = nextKey }
                postDao.insertAll(entities)
            }

            // TODO: return Success only after data will be stored in database
            delay(1000)
            MediatorResult.Success(endOfPaginationReached = feedPageResponse.size < state.config.pageSize)
        } catch (e: Exception) {
            Timber.tag(tag).w(e)
            MediatorResult.Error(e)
        }
    }
}