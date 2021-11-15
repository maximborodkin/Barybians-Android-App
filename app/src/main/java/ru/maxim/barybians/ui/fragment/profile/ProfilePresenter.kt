package ru.maxim.barybians.ui.fragment.profile

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.ui.fragment.base.BaseWallPresenter

@InjectViewState
class ProfilePresenter : BaseWallPresenter<ProfileView>() {
    private val retrofitClient: RetrofitClient by inject(RetrofitClient::class.java)

    fun loadUser(userId: Int) {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
        }
        launch {
            try {
                val loadUserResponse = userService.getUser(userId)
                if (loadUserResponse.isSuccessful && loadUserResponse.body() != null){
                    viewState.showUserProfile(loadUserResponse.body()!!)
                } else {
                    viewState.onUserLoadError()
                }
            } catch (e: Exception) {
                viewState.onUserLoadError()
            }
        }
    }

    fun createPost(title: String?, text: String) {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
        }
        launch {
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
    }

    fun editStatus(newStatus: String?) {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
        }
        launch {
            try {
                val status = userService.editStatus(newStatus)
                if (status.isSuccessful && status.body() == "true") {
                    viewState.onStatusEdited(newStatus)
                }
            } catch (e: Exception) { }
        }
    }
}