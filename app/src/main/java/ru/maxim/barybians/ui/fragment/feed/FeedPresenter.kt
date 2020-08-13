package ru.maxim.barybians.ui.fragment.feed

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter

@InjectViewState
open class FeedPresenter : BaseWallPresenter<FeedView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadFeed()
    }

    fun loadFeed() {
        if (!RetrofitClient.isOnline()) {
            viewState.showNoInternet()
            return
        }
        launch {
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
}