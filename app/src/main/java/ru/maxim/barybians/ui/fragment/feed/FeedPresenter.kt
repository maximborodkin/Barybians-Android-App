package ru.maxim.barybians.ui.fragment.feed

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.CommentService
import ru.maxim.barybians.repository.remote.service.PostService

@InjectViewState
class FeedPresenter : MvpPresenter<FeedView>(), CoroutineScope by MainScope() {

    private val postService: PostService = PostService()
    private val commentService: CommentService by lazy { CommentService() }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadFeed()
    }

    fun loadFeed() {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val loadFeedResponse = postService.getFeed()
                if (loadFeedResponse.isSuccessful && loadFeedResponse.body() != null){
                    viewState.showFeed(loadFeedResponse.body()!!)
                } else {
                    viewState.onFeedLoadError()
                }
            } catch (e: Exception) {
                viewState.onFeedLoadError()
            }
        }
    }

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

    fun addComment(postId: Int, postPosition: Int, commentsCount: Int, text: String) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val comment = commentService.addComment(postId, text)
                if (comment.isSuccessful && comment.body() != null) {
                    viewState.onCommentAdded(postPosition, commentsCount, comment.body()!!)
                } else {
                    viewState.onCommentAddError()
                }
            } catch (e: Exception) {
                viewState.onCommentAddError()
            }
        }
    }

    fun deleteComment(postPosition: Int, commentsCount: Int, commentId: Int, commentPosition: Int) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val deleteCommentRequest = commentService.deleteComment(commentId)
                if (deleteCommentRequest.isSuccessful && deleteCommentRequest.body() == "true") {
                    viewState.onCommentDeleted(postPosition, commentsCount, commentPosition)
                } else {
                    viewState.onCommentDeleteError()
                }
            } catch (e: Exception) {
                viewState.onCommentDeleteError()
            }
        }
    }

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