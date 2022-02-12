package ru.maxim.barybians.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.persistence.database.dao.PostDao
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.NetworkUtils
import timber.log.Timber
import java.security.spec.InvalidKeySpecException
import javax.inject.Inject

class FeedPagingSource private constructor(
    private val postDao: PostDao
) : PagingSource<Int, PostEntity>() {

    override fun getRefreshKey(state: PagingState<Int, PostEntity>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostEntity> {
        Timber.d("XXX FeedPagingSource load with page ${params.key}")
        delay(2000)
        val page = params.key ?: 0

        return try {
            val feed = postDao.getFeedPage(page)
            val nextKey = if (feed.size < params.loadSize) null else page + 1
            val prevKey = if (page == 0) null else page - 1
            LoadResult.Page(data = feed, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    class FeedPagingSourceFactory @Inject constructor(
        private val postDao: PostDao,
    ) {
        fun create() = FeedPagingSource(postDao)
    }
}