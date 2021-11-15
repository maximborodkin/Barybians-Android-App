package ru.maxim.barybians.ui.fragment.feed

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter

@InjectViewState
open class FeedPresenter : BaseWallPresenter<FeedView>() {
    private val retrofitClient: RetrofitClient by inject(RetrofitClient::class.java)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadFeed()
    }

    fun loadFeed() {
        if (!retrofitClient.isOnline()) {
            return viewState.showNoInternet()
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