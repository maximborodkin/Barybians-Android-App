package ru.maxim.barybians.ui.fragment.feed

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.like.LikeRepository
import ru.maxim.barybians.data.repository.post.PostRepository
import ru.maxim.barybians.domain.model.Post
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
open class FeedViewModel constructor(
    application: Application,
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
) : AndroidViewModel(application) {

    // When your wings are burning, who keeps you from falling?
    open val postsList: StateFlow<PagingData<Post>> = postRepository.getFeedPager()
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    protected val mErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = mErrorMessage

    open val postsCount: StateFlow<Int> = postRepository.getPostsCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun createPost(title: String?, text: String) = viewModelScope.launch {
        try {
            val uuid = UUID.randomUUID().toString()
            postRepository.createPost(uuid, title, text)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_create_post
            }
            mErrorMessage.postValue(errorMessageRes)
        }
    }


    fun editPost(postId: Int, title: String?, text: String) = viewModelScope.launch {
        try {
            postRepository.editPost(postId, title, text)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_edit_post
            }
            mErrorMessage.postValue(errorMessageRes)
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
            mErrorMessage.postValue(errorMessageRes)
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
            mErrorMessage.postValue(errorMessageRes)
        }
    }

    class FeedViewModelFactory @Inject constructor(
        private val application: Application,
        private val postRepository: PostRepository,
        private val likeRepository: LikeRepository,
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                return FeedViewModel(
                    application = application,
                    postRepository = postRepository,
                    likeRepository = likeRepository,
                ) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}