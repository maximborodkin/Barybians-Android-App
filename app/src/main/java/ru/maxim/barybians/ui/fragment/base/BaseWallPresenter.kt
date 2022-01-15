package ru.maxim.barybians.ui.fragment.base

import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.ui.fragment.feed.FeedFragment

abstract class BaseWallPresenter<T : BaseWallView>(
    private val postRepository: PostRepository
) : MvpPresenter<T>() {

    /**
     * id and layout position of currently shown post
     * Used to restore BottomSheetDialog state.
     * If dialog was shown before the state has changed, it will be restored by this parameters.
     * Passed to [FeedFragment.showCommentsList]
     * If value is -1, dialog will not appear
     */
    var currentPostId: Int = 0
    var currentPostPosition: Int = 0

//    protected val userService: UserService by inject(UserService::class.java)
//    protected val postService: PostService by inject(PostService::class.java)
//    private val commentService: CommentService by inject(CommentService::class.java)
//    private val retrofitClient: RetrofitClient by inject(RetrofitClient::class.java)

    fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) =
        presenterScope.launch {
            try {
                val updatedPostResponse = postRepository.updatePost(postId, newTitle, newText)
                viewState.onPostUpdated(itemPosition, updatedPostResponse)
            } catch (e: Exception) {
                viewState.onPostUpdateError()
            }
        }

    fun deletePost(itemPosition: Int, postId: Int) = presenterScope.launch {
        try {
            if (postRepository.deletePost(postId)) {
                viewState.onPostDeleted(itemPosition)
            } else {
                viewState.onPostDeleteError()
            }
        } catch (e: Exception) {
            viewState.onPostDeleteError()
        }
    }

    fun addComment(postId: Int, postPosition: Int, text: String) = presenterScope.launch {
//        try {
//            val comment = commentService.addComment(postId, text)
//            if (comment.isSuccessful && comment.body() != null) {
//                viewState.onCommentAdded(postPosition, comment.body()!!)
//            } else {
//                viewState.onCommentAddError()
//            }
//        } catch (e: Exception) {
//            viewState.onCommentAddError()
//        }
    }

    fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int) =
        presenterScope.launch {
//            if (!retrofitClient.isOnline()) {
//                return@launch viewState.showNoInternet()
//            }
//            try {
//                val deleteCommentRequest = commentService.deleteComment(commentId)
//                if (deleteCommentRequest.isSuccessful && deleteCommentRequest.body() == "true") {
//                    viewState.onCommentDeleted(postPosition, commentPosition, commentId)
//                } else {
//                    viewState.onCommentDeleteError()
//                }
//            } catch (e: Exception) {
//                viewState.onCommentDeleteError()
//            }
        }


    fun editLike(itemPosition: Int, postId: Int, hasLike: Boolean) = presenterScope.launch {
        try {
            val editLikeResponse =
                if (hasLike) postRepository.setLike(postId)
                else postRepository.removeLike(postId)
            viewState.onLikeEdited(itemPosition, editLikeResponse.whoLiked)
        } catch (e: Exception) {
        }
    }
}