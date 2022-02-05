package ru.maxim.barybians.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.NetworkUtils
import timber.log.Timber
import javax.inject.Inject

class FeedPagingSource @Inject constructor(
    private val postRepository: PostRepository,
    private val networkUtils: NetworkUtils
) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        Timber.tag("PAGING_FEED").d("getRefreshKey")
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(state.config.pageSize)
            ?: page.nextKey?.minus(state.config.pageSize)
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

    //    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
//        if (!networkUtils.isOnline()) return LoadResult.Error(NoConnectionException())
//
//        val startIndex = params.key ?: 0
//        val endIndex = startIndex + params.loadSize//.coerceAtMost(PostRepository.maxPageSize)
//        Timber.tag("PAGING_FEED").d("Load page with key: ${params.key} and loadSize: ${params.loadSize}. startIndex: $startIndex, endIndex: $endIndex")
//
//        return try {
//            val feed = postRepository.loadFeedPage(startIndex, endIndex)
//            val nextKey = if (feed.size < params.loadSize) null else startIndex + params.loadSize
//            val prevKey = if (startIndex == 0) null else startIndex - params.loadSize
//            Timber.tag("PAGING_FEED").d("Loaded page with size ${feed.size}, nextKey $nextKey, prevKey: $prevKey")
//            Timber.tag("PAGING_FEED").d("Loaded posts: ${feed.map { "${it.id} ${Date(it.date * 1000).date()}" }.joinToString("\n")}")
//            LoadResult.Page(feed, nextKey, prevKey)
//        } catch (e: Exception) {
//            Timber.tag("PAGING_FEED").d("Error while loading page with key: ${params.key} and loadSize: ${params.loadSize}")
//            Timber.tag("PAGING_FEED").e(e)
//            LoadResult.Error(e)
//        }
//    }
}