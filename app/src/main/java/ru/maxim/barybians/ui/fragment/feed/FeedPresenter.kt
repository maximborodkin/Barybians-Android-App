package ru.maxim.barybians.ui.fragment.feed

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import ru.maxim.barybians.data.repository.CommentRepository
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.data.repository.UserRepository
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter
import javax.inject.Inject

@InjectViewState
open class FeedPresenter @Inject constructor(
    private val postRepository: PostRepository,
    userRepository: UserRepository,
    commentRepository: CommentRepository
) : BaseWallPresenter<FeedView>(postRepository, userRepository, commentRepository) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadFeed()
    }

    fun loadFeed() = presenterScope.launch {
        try {
            val loadFeedResponse = postRepository.getFeed()
            viewState.showFeed(loadFeedResponse)
        } catch (e: Exception) {
            viewState.onFeedLoadError()
        }
    }
}