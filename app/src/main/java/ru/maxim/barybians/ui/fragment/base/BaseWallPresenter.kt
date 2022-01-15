package ru.maxim.barybians.ui.fragment.base

import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.repository.CommentRepository
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.data.repository.UserRepository
import ru.maxim.barybians.ui.fragment.feed.FeedFragment

abstract class BaseWallPresenter<T : BaseWallView>(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
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

    fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) =
        presenterScope.launch {
            try {
                val updatedPostResponse = postRepository.updatePost(postId, newTitle, newText)
                viewState.onPostUpdated(itemPosition, updatedPostResponse)
            } catch (e: Exception) {
                when (e) {
                    is NoConnectionException -> viewState.showNoInternet()
                    else -> viewState.onPostUpdateError()
                }
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
            when (e) {
                is NoConnectionException -> viewState.showNoInternet()
                else -> viewState.onPostDeleteError()
            }
        }
    }

    fun createComment(postId: Int, postPosition: Int, text: String) = presenterScope.launch {
        try {
            val comment = commentRepository.createComment(postId, text)
            viewState.onCommentAdded(postPosition, comment)
        } catch (e: Exception) {
            when (e) {
                is NoConnectionException -> viewState.showNoInternet()
                else -> viewState.onCommentAddError()
            }
        }
    }

    fun editComment(commentId: Int, text: String) = presenterScope.launch {
        try {
            val comment = commentRepository.editComment(commentId, text)
            viewState.onCommentEdit(comment)
        } catch (e: Exception) {
            when (e) {
                is NoConnectionException -> viewState.showNoInternet()
                else -> viewState.onCommentEditError()
            }
        }
    }

    fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int) =
        presenterScope.launch {
            try {
                if (commentRepository.deleteComment(commentId)) {
                    viewState.onCommentDeleted(postPosition, commentPosition, commentId)
                } else {
                    viewState.onCommentDeleteError()
                }
            } catch (e: Exception) {
                when (e) {
                    is NoConnectionException -> viewState.showNoInternet()
                    else -> viewState.onCommentDeleteError()
                }
            }
        }

    fun editLike(itemPosition: Int, postId: Int, hasLike: Boolean) = presenterScope.launch {
        try {
            val editLikeResponse =
                if (hasLike) postRepository.setLike(postId)
                else postRepository.removeLike(postId)
            viewState.onLikeEdited(itemPosition, editLikeResponse.whoLiked)
        } catch (e: Exception) {
            when (e) {
                is NoConnectionException -> viewState.showNoInternet()
                else -> viewState.onLikeEditError()
            }
        }
    }
}