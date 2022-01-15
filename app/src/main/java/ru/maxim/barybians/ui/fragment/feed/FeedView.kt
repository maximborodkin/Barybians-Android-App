package ru.maxim.barybians.ui.fragment.feed

import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.fragment.base.BaseWallView

interface FeedView : BaseWallView {

    @AddToEnd
    fun showFeed(posts: ArrayList<Post>)

    @OneExecution
    fun onFeedLoadError()
}