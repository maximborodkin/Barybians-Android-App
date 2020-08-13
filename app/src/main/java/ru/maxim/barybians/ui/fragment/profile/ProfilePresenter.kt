package ru.maxim.barybians.ui.fragment.profile

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter

@InjectViewState
class ProfilePresenter : BaseWallPresenter<ProfileView>() {

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