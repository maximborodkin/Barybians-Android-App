package ru.maxim.barybians.ui.fragment.feed

import com.arellomobile.mvp.MvpView
import ru.maxim.barybians.model.Post
import ru.maxim.barybians.model.User
import ru.maxim.barybians.model.response.CommentResponse

interface FeedView : MvpView {

    fun showNoInternet()
    fun showLoading()
    fun showFeed(posts: ArrayList<Post>)
    fun onFeedLoadError()
    fun onPostUpdated(itemPosition: Int, post: Post)
    fun onPostUpdateError()
    fun onPostDeleted(itemPosition: Int)
    fun onPostDeleteError()
    fun onCommentAdded(postPosition: Int, commentsCount: Int, comment: CommentResponse)
    fun onCommentAddError()
    fun onCommentDeleted(postPosition: Int, commentsCount: Int, commentPosition: Int)
    fun onCommentDeleteError()
    fun onLikeEdited(postPosition: Int, likedUsers: ArrayList<User>)
}