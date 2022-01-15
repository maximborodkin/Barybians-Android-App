package ru.maxim.barybians.ui.fragment.base

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.maxim.barybians.data.network.response.CommentResponse
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User

interface BaseWallView : MvpView {

    @OneExecution
    fun showNoInternet()

    @AddToEndSingle
    fun showLoading()

    @AddToEnd
    fun onPostUpdated(itemPosition: Int, post: Post)

    @OneExecution
    fun onPostUpdateError()

    @AddToEnd
    fun onPostDeleted(itemPosition: Int)

    @OneExecution
    fun onPostDeleteError()

    @AddToEnd
    fun onCommentAdded(postPosition: Int, comment: CommentResponse)

    @OneExecution
    fun onCommentAddError()

    @AddToEnd
    fun onCommentDeleted(postPosition: Int, commentPosition: Int, commentId: Int)

    @OneExecution
    fun onCommentDeleteError()

    @AddToEnd
    fun onLikeEdited(postPosition: Int, likedUsers: ArrayList<User>)
}