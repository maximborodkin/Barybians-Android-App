package ru.maxim.barybians.ui.fragment.profile

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.CommentService
import ru.maxim.barybians.repository.remote.service.PostService
import ru.maxim.barybians.repository.remote.service.UserService

@InjectViewState
class ProfilePresenter : MvpPresenter<ProfileView>(), CoroutineScope by MainScope() {

    private val userService = UserService()
    private val postService: PostService by lazy { PostService() }
    private val commentService: CommentService by lazy { CommentService() }

    fun loadUser(userId: Int) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val loadUserResponse = userService.getUser(userId)
                if (loadUserResponse.isSuccessful && loadUserResponse.body() != null){
                    viewState.showUserProfile(loadUserResponse.body()!!)
                }
            } catch (e: Exception) {
                viewState.onUserLoadError()
            }
        }
    }

    fun createPost(title: String?, text: String) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val createPostResponse = postService.createPost(title, text)
                if (createPostResponse.isSuccessful && createPostResponse.body() != null) {
                    viewState.onPostCreated(createPostResponse.body()!!)
                }
            } catch (e: Exception) {
                viewState.onPostCreateError()
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
                Log.d("ProfilePresenter", "Start adding comment, postId: ${postId}, postPosition: ${postPosition}, commentsCount: ${commentsCount}, text: ${text}")
                val comment = commentService.addComment(postId, text)
                Log.d("ProfilePresenter", "Comment request sended")
                if (comment.isSuccessful && comment.body() != null) {
                    Log.d("ProfilePresenter", "Comment request successful, comment.body: ${comment.body()}")
                    viewState.onCommentAdded(postPosition, commentsCount, comment.body()!!)
                } else {
                    Log.d("ProfilePresenter", "Comment request failed, comment.code: ${comment.code()}")
                    viewState.onCommentAddError()
                }
            } catch (e: Exception) {
                Log.d("ProfilePresenter", "Comment request failed, exception: ${e.localizedMessage}")
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

    fun editStatus(newStatus: String?) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoInternet()
            return
        }
        launch {
            try {
                val status = userService.editStatus(newStatus)
                if (status.isSuccessful && status.body() == "true") {
                    viewState.onStatusEdited(newStatus)
                }
            } catch (e: Exception) {

            }
        }
    }
}