package ru.maxim.barybians.ui.fragment.feed

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.PostRepository
import javax.inject.Inject

open class FeedViewModel constructor(
    application: Application,
    protected val postRepository: PostRepository
) : AndroidViewModel(application) {

    protected val internalIsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = internalIsLoading

    protected val internalMessageRes: MutableLiveData<Int?> = MutableLiveData(null)
    val messageRes: LiveData<Int?> = internalMessageRes

    val posts = postRepository.posts

    init {
        initialLoading()
    }

    protected open fun initialLoading() {
        loadFeed()
    }

    fun loadFeed() = viewModelScope.launch {
        internalIsLoading.postValue(true)
        try {
            postRepository.loadPosts()
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.an_error_occurred_while_loading_feed
            }
            internalMessageRes.postValue(errorMessageRes)
        } finally {
            internalIsLoading.postValue(false)
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
            internalMessageRes.postValue(errorMessageRes)
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
            internalMessageRes.postValue(errorMessageRes)
        }
    }

    fun changeLike(postId: Int) = viewModelScope.launch {
        try {
            postRepository.changeLike(postId)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_change_like
            }
            internalMessageRes.postValue(errorMessageRes)
        }
    }

    class FeedViewModelFactory @Inject constructor(
        private val application: Application,
        private val postRepository: PostRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FeedViewModel(application, postRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}