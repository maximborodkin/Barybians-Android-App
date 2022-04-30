package ru.maxim.barybians.ui.fragment.postsList

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.network.model.ParseMode
import ru.maxim.barybians.data.repository.like.LikeRepository
import ru.maxim.barybians.data.repository.post.PostRepository
import ru.maxim.barybians.domain.model.Post
import java.util.*

@OptIn(ExperimentalPagingApi::class)
open class PostsListViewModel constructor(
    application: Application,
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    val userId: Int?
) : AndroidViewModel(application) {

    // When your wings are burning, who keeps you from falling?
    open val postsList: StateFlow<PagingData<Post>> =
        (if (userId != null && userId > 0) postRepository.getUserPostsPager(userId)
        else postRepository.getFeedPager())
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    private val _errorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = _errorMessage

    open val postsCount: StateFlow<Int> = postRepository.getPostsCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun createPost(title: String?, text: String) = viewModelScope.launch {
        try {
            val uuid = UUID.randomUUID().toString()
            postRepository.createPost(parseMode = ParseMode.MD, uuid = uuid, title = title, text = text)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_create_post
            }
            _errorMessage.postValue(errorMessageRes)
        }
    }

    fun editPost(postId: Int, title: String?, text: String) = viewModelScope.launch {
        try {
            postRepository.editPost(parseMode = ParseMode.MD, postId = postId, title = title, text = text)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_edit_post
            }
            _errorMessage.postValue(errorMessageRes)
        }
    }

    fun deletePost(postId: Int) = viewModelScope.launch {
        try {
            postRepository.deletePost(postId)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_delete_post
            }
            _errorMessage.postValue(errorMessageRes)
        }
    }

    fun changeLike(postId: Int) = viewModelScope.launch {
        try {
            likeRepository.changeLike(postId)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_change_like
            }
            _errorMessage.postValue(errorMessageRes)
        }
    }

    class PostsListViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val postRepository: PostRepository,
        private val likeRepository: LikeRepository,
        @Assisted(PostsListFragment.userIdKey) private val userId: Int?
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostsListViewModel::class.java)) {
                return PostsListViewModel(
                    application = application,
                    postRepository = postRepository,
                    likeRepository = likeRepository,
                    userId = userId
                ) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted(PostsListFragment.userIdKey) userId: Int?): PostsListViewModelFactory
        }
    }
}