package ru.maxim.barybians.ui.fragment.base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.CommentService
import ru.maxim.barybians.repository.remote.service.PostService
import ru.maxim.barybians.repository.remote.service.UserService

open class BaseWallPresenter<T : BaseWallView> : MvpPresenter<T>(), CoroutineScope by MainScope() {

    protected val userService = UserService()
    protected val postService: PostService by lazy { PostService() }
    private val commentService: CommentService by lazy { CommentService() }

    fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val updatedPostResponse = postService.updatePost(postId, newTitle, newText)
                if (updatedPostResponse.isSuccessful && updatedPostResponse.body() != null) {
                    viewState.onPostUpdated(itemPosition, updatedPostResponse.body()!!)
                }
            } catch (e: Exception) {
                viewState.onPostUpdateError()
            }
        }
    }

    fun deletePost(itemPosition: Int, postId: Int) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val deletePostResponse = postService.deletePost(postId)
                if (deletePostResponse.isSuccessful && deletePostResponse.body() == "true"){
                    viewState.onPostDeleted(itemPosition)
                } else {
                    viewState.onPostDeleteError()
                }
            } catch (e: Exception) {
                viewState.onPostDeleteError()
            }
        }
    }

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addComment(postId: Int, postPosition: Int, text: String) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val comment = commentService.addComment(postId, text)
                if (comment.isSuccessful && comment.body() != null) {
                    viewState.onCommentAdded(postPosition, comment.body()!!)
                } else {
                    viewState.onCommentAddError()
                }
            } catch (e: Exception) {
                viewState.onCommentAddError()
            }
        }
    }

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val deleteCommentRequest = commentService.deleteComment(commentId)
                if (deleteCommentRequest.isSuccessful && deleteCommentRequest.body() == "true") {
                    viewState.onCommentDeleted(postPosition, commentPosition)
                } else {
                    viewState.onCommentDeleteError()
                }
            } catch (e: Exception) {
                viewState.onCommentDeleteError()
            }
        }
    }

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun editLike(itemPosition: Int, postId: Int, hasLike: Boolean) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val editLikeResponse =
                    if (hasLike) postService.addLike(postId)
                    else postService.removeLike(postId)
                if (editLikeResponse.isSuccessful && editLikeResponse.body() != null) {
                    viewState.onLikeEdited(itemPosition, editLikeResponse.body()!!.likedUsers)
                }
            } catch (e: Exception) {

            }
        }
    }
}