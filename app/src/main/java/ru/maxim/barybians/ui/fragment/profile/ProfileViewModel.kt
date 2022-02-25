package ru.maxim.barybians.ui.fragment.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.post.PostRemoteMediator
import ru.maxim.barybians.data.repository.post.PostRemoteMediator.PostRemoteMediatorFactory
import ru.maxim.barybians.data.repository.like.LikeRepository
import ru.maxim.barybians.data.repository.post.PostRepository
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.feed.FeedViewModel

class ProfileViewModel(
    application: Application,
    postRepository: PostRepository,
    likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val userId: Int
) : FeedViewModel(application, postRepository, likeRepository) {

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    override val postsList: StateFlow<PagingData<Post>> = postRepository.getUserPostsPager(userId)
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    override val postsCount: StateFlow<Int> = postRepository.getPostsCount(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        updateUser()
    }

    fun updateUser() = viewModelScope.launch {
        try {
            delay(4000)
            _user.emit(userRepository.getUserById(userId))
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.an_error_occurred_while_loading_profile
            }
            mErrorMessage.postValue(errorMessageRes)
        }
    }

    fun editStatus(status: String) = viewModelScope.launch {
        try {
            userRepository.editStatus(status)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_edit_status
            }
            mErrorMessage.postValue(errorMessageRes)
        }
    }

    class ProfileViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val postRepository: PostRepository,
        private val likeRepository: LikeRepository,
        private val preferencesManager: PreferencesManager,
        private val userRepository: UserRepository,
        @Assisted("userId") private val userId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                val userId = if (userId <= 0) preferencesManager.userId else userId

                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(
                    application,
                    postRepository,
                    likeRepository,
                    userRepository,
                    userId
                ) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("userId") userId: Int): ProfileViewModelFactory
        }
    }
}