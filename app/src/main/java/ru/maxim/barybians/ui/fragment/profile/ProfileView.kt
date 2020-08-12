package ru.maxim.barybians.ui.fragment.profile

import com.arellomobile.mvp.MvpView
import ru.maxim.barybians.model.Post
import ru.maxim.barybians.model.User
import ru.maxim.barybians.model.response.CommentResponse

interface ProfileView : MvpView{

    fun showNoInternet()
    fun showLoading()
    fun showUserProfile(user: User)
    fun onUserLoadError()
    fun onStatusEdited(newStatus: String?)
    fun onPostCreated(post: Post)
    fun onPostCreateError()
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