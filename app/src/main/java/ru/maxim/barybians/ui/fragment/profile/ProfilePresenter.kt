package ru.maxim.barybians.ui.fragment.profile

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor(private val retrofitClient: RetrofitClient) :
    BaseWallPresenter<ProfileView>() {

    fun loadUser(userId: Int) = presenterScope.launch {
        if (!retrofitClient.isOnline()) {
            return@launch viewState.showNoInternet()
        }
        try {
            val loadUserResponse = userService.getUser(userId)
            if (loadUserResponse.isSuccessful && loadUserResponse.body() != null) {
                viewState.showUserProfile(loadUserResponse.body()!!)
            } else {
                viewState.onUserLoadError()
            }
        } catch (e: Exception) {
            viewState.onUserLoadError()
        }

    }

    fun createPost(title: String?, text: String) = presenterScope.launch {
        if (!retrofitClient.isOnline()) {
            return@launch viewState.showNoInternet()
        }
        try {
            val createPostResponse = postService.createPost(title, text)
            if (createPostResponse.isSuccessful && createPostResponse.body() != null) {
                viewState.onPostCreated(createPostResponse.body()!!)
            } else {
                viewState.onPostCreated(createPostResponse.body()!!)
            }
        } catch (e: Exception) {
            viewState.onPostCreateError()
        }
    }

    fun editStatus(newStatus: String?) = presenterScope.launch {
        if (!retrofitClient.isOnline()) {
            return@launch viewState.showNoInternet()
        }
        try {
            val status = userService.editStatus(newStatus)
            if (status.isSuccessful && status.body() == "true") {
                viewState.onStatusEdited(newStatus)
            }
        } catch (e: Exception) { }
    }
}