package ru.maxim.barybians.ui.fragment.feed

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter
import javax.inject.Inject

@InjectViewState
open class FeedPresenter @Inject constructor(private val retrofitClient: RetrofitClient) :
    BaseWallPresenter<FeedView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadFeed()
    }

    fun loadFeed() = presenterScope.launch {
        if (!retrofitClient.isOnline()) {
            return@launch viewState.showNoInternet()
        }
        try {
            val loadFeedResponse = postService.getFeed()
            if (loadFeedResponse.isSuccessful && loadFeedResponse.body() != null) {
                viewState.showFeed(loadFeedResponse.body()!!)
            } else {
                viewState.onFeedLoadError()
            }
        } catch (e: Exception) {
            viewState.onFeedLoadError()
        }

    }
}