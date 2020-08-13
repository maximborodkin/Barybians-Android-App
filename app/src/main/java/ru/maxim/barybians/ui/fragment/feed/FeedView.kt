package ru.maxim.barybians.ui.fragment.feed

import ru.maxim.barybians.model.Post
import ru.maxim.barybians.ui.fragment.base.BaseWallView

interface FeedView : BaseWallView {

    fun showFeed(posts: ArrayList<Post>)
    fun onFeedLoadError()
}