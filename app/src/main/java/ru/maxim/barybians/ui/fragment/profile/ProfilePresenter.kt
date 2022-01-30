package ru.maxim.barybians.ui.fragment.profile

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import ru.maxim.barybians.data.repository.CommentRepository
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.data.repository.UserRepository
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor(
    private val postRepository: PostRepository,
    userRepository: UserRepository,
    commentRepository: CommentRepository
) : BaseWallPresenter<ProfileView>(postRepository, userRepository, commentRepository) {

//    fun loadUser(userId: Int) = presenterScope.launch {
//        try {
//            val loadUserResponse = userService.getUser(userId)
//            if (loadUserResponse.isSuccessful && loadUserResponse.body() != null) {
//                viewState.showUserProfile(loadUserResponse.body()!!)
//            } else {
//                viewState.onUserLoadError()
//            }
//        } catch (e: Exception) {
//            viewState.onUserLoadError()
//        }

//    }

//    fun createPost(title: String?, text: String) = presenterScope.launch {
//        try {
//            val createPostResponse = postRepository.createPost(title, text)
//            viewState.onPostCreated(createPostResponse)
//        } catch (e: Exception) {
//            viewState.onPostCreateError()
//        }
//    }
//
//    fun editStatus(newStatus: String?) = presenterScope.launch {
////        try {
////            val status = userService.editStatus(newStatus)
////            if (status.isSuccessful && status.body() == "true") {
////                viewState.onStatusEdited(newStatus)
////            }
////        } catch (e: Exception) {
////        }
//    }
}