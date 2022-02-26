package ru.maxim.barybians.ui.fragment.base

import moxy.MvpPresenter
import ru.maxim.barybians.data.repository.comment.CommentRepository
import ru.maxim.barybians.data.repository.post.PostRepository
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.ui.fragment.feed.PostsListFragment

abstract class BaseWallPresenter<T : BaseWallView>(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
) : MvpPresenter<T>() {

    /**
     * postId and layout position of currently shown post
     * Used to restore BottomSheetDialog state.
     * If dialog was shown before the state has changed, it will be restored by this parameters.
     * Passed to [PostsListFragment.onCommentsClick]
     * If value is -1, dialog will not appear
     */
//    var currentPostId: Int = 0
//    var currentPostPosition: Int = 0

//    fun editPost(postId: Int, newTitle: String?, newText: String) {
//        presenterScope.launch {
//            try {
//                val updatedPostResponse = postRepository.updatePost(postId, newTitle, newText)
//                viewState.onPostUpdated(updatedPostResponse)
//            } catch (e: Exception) {
//                when (e) {
//                    is NoConnectionException -> viewState.showNoInternet()
//                    else -> viewState.onPostUpdateError()
//                }
//            }
//        }
//    }
//
//    fun deletePost(postId: Int) {
//        presenterScope.launch {
//            try {
//                if (postRepository.deletePost(postId)) viewState.onPostDeleted(postId)
//                else viewState.onPostDeleteError()
//            } catch (e: Exception) {
//                when (e) {
//                    is NoConnectionException -> viewState.showNoInternet()
//                    else -> viewState.onPostDeleteError()
//                }
//            }
//        }
//    }
//
//    fun createComment(postId: Int, text: String) {
//        presenterScope.launch {
//            try {
//                val uuid = UUID.randomUUID().toString()
//                val commentId = commentRepository.createComment(uuid, postId, text)
////                viewState.onCommentAdded(postId, comment)
//            } catch (e: Exception) {
//                when (e) {
//                    is NoConnectionException -> viewState.showNoInternet()
//                    else -> viewState.onCommentAddError()
//                }
//            }
//        }
//    }
//
//    fun editComment(commentId: Int, text: String) {
//        presenterScope.launch {
//            try {
//                val comment = commentRepository.editComment(commentId, text)
//                viewState.onCommentEdit(comment)
//            } catch (e: Exception) {
//                when (e) {
//                    is NoConnectionException -> viewState.showNoInternet()
//                    else -> viewState.onCommentEditError()
//                }
//            }
//        }
//    }
//
//    fun deleteComment(commentId: Int) {
//        presenterScope.launch {
//            try {
//                if (commentRepository.deleteComment(commentId)) viewState.onCommentDeleted(commentId)
//                else viewState.onCommentDeleteError()
//            } catch (e: Exception) {
//                when (e) {
//                    is NoConnectionException -> viewState.showNoInternet()
//                    else -> viewState.onCommentDeleteError()
//                }
//            }
//        }
//    }
//
//    fun editLike(postId: Int, hasLike: Boolean) {
//        presenterScope.launch {
//            try {
//                val editLikeResponse =
//                    if (hasLike) postRepository.setLike(postId)
//                    else postRepository.removeLike(postId)
//                viewState.onLikeEdited(postId, editLikeResponse.whoLiked)
//            } catch (e: Exception) {
//                when (e) {
//                    is NoConnectionException -> viewState.showNoInternet()
//                    else -> viewState.onLikeEditError()
//                }
//            }
//        }
//    }
}