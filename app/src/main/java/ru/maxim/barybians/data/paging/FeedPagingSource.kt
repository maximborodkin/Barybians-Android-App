package ru.maxim.barybians.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.NetworkUtils
import javax.inject.Inject

class FeedPagingSource private constructor(
    private val postRepository: PostRepository,
    private val networkUtils: NetworkUtils
) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        if (!networkUtils.isOnline()) return LoadResult.Error(NoConnectionException())

        val page = params.key ?: 0

        return try {
            val feed = postRepository.loadFeedPage(page * params.loadSize, params.loadSize)
            val nextKey = if (feed.size < params.loadSize) null else page + 1
            val prevKey = if (page == 0) null else page - 1
            LoadResult.Page(data = feed, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    class FeedPagingSourceFactory @Inject constructor(
        private val postRepository: PostRepository,
        private val networkUtils: NetworkUtils
    ) {
        fun create() = FeedPagingSource(postRepository, networkUtils)
    }
}