package ru.maxim.barybians.ui.fragment.base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.network.service.CommentService
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.network.service.UserService
import ru.maxim.barybians.ui.fragment.feed.FeedFragment

open class BaseWallPresenter<T : BaseWallView> : MvpPresenter<T>(), CoroutineScope by MainScope() {

    /**
     * id and layout position of post that currently shown on view
     * Used to restore BottomSheetDialog state.
     * If dialog was shown before change state, it will be restored by this parameters.
     * Passed to [FeedFragment.showCommentsList]
     * If value is -1, dialog will not appear
     */
    var currentPostId: Int = 0
    var currentPostPosition: Int = 0

    protected val userService: UserService by inject(UserService::class.java)
    protected val postService: PostService by inject(PostService::class.java)
    private val commentService: CommentService by inject(CommentService::class.java)
    private val retrofitClient: RetrofitClient by inject(RetrofitClient::class.java)

    fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
        }
        launch {
            try {
                val updatedPostResponse = postService.updatePost(postId, newTitle, newText)
                if (updatedPostResponse.isSuccessful && updatedPostResponse.body() != null) {
                    viewState.onPostUpdated(itemPosition, updatedPostResponse.body()!!)
                } else {
                    viewState.onPostUpdateError()
                }
            } catch (e: Exception) {
                viewState.onPostUpdateError()
            }
        }
    }

    fun deletePost(itemPosition: Int, postId: Int) {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
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

    fun addComment(postId: Int, postPosition: Int, text: String) {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
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

    fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int) {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
        }
        launch {
            try {
                val deleteCommentRequest = commentService.deleteComment(commentId)
                if (deleteCommentRequest.isSuccessful && deleteCommentRequest.body() == "true") {
                    viewState.onCommentDeleted(postPosition, commentPosition, commentId)
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
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
        }
        launch {
            try {
                val editLikeResponse =
                    if (hasLike) postService.addLike(postId)
                    else postService.removeLike(postId)
                if (editLikeResponse.isSuccessful && editLikeResponse.body() != null) {
                    viewState.onLikeEdited(itemPosition, editLikeResponse.body()!!.likedUsers)
                }
            } catch (e: Exception) { }
        }
    }
}