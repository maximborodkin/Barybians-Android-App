package ru.maxim.barybians.ui.fragment.base

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User

interface BaseWallView : MvpView {

    @OneExecution
    fun showNoInternet()

    @AddToEndSingle
    fun showLoading()

    @AddToEnd
    fun onPostUpdated(post: Post)

    @OneExecution
    fun onPostUpdateError()

    @AddToEnd
    fun onPostDeleted(postId: Int)

    @OneExecution
    fun onPostDeleteError()

    @AddToEnd
    fun onLikeEdited(postId: Int, likedUsers: List<User>)

    @OneExecution
    fun onLikeEditError()
}