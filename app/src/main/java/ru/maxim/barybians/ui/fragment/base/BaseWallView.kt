package ru.maxim.barybians.ui.fragment.base

import com.arellomobile.mvp.MvpView
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.data.network.response.CommentResponse

interface BaseWallView : MvpView{

    fun showNoInternet()
    fun showLoading()
    fun onPostUpdated(itemPosition: Int, post: Post)
    fun onPostUpdateError()
    fun onPostDeleted(itemPosition: Int)
    fun onPostDeleteError()
    fun onCommentAdded(postPosition: Int, comment: CommentResponse)
    fun onCommentAddError()
    fun onCommentDeleted(postPosition: Int, commentPosition: Int, commentId: Int)
    fun onCommentDeleteError()
    fun onLikeEdited(postPosition: Int, likedUsers: ArrayList<User>)
}