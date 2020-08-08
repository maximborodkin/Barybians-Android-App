package ru.maxim.barybians.ui.fragment.feed

import com.arellomobile.mvp.MvpView

interface FeedView : MvpView {

    fun showFeed(/*feed: List<Post>*/)
}