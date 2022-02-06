package ru.maxim.barybians.ui.fragment.feed

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.paging.FeedPagingSource.FeedPagingSourceFactory
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.domain.model.Post
import javax.inject.Inject

open class FeedViewModel constructor(
    application: Application,
    protected val postRepository: PostRepository,
    private val feedPagingSourceFactory: FeedPagingSourceFactory
) : AndroidViewModel(application) {

    val feed: StateFlow<PagingData<Post>> = Pager(
        config = PagingConfig(
            initialLoadSize = PostRepository.pageSize,
            pageSize = PostRepository.pageSize,
            prefetchDistance = PostRepository.prefetchDistance,
            enablePlaceholders = true
        ),
        pagingSourceFactory = feedPagingSourceFactory::create
    )
        .flow
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    private val _messageRes: MutableLiveData<Int?> = MutableLiveData(null)
    val messageRes: LiveData<Int?> = _messageRes

    fun editPost(postId: Int, title: String?, text: String) = viewModelScope.launch {
        try {
            postRepository.editPost(postId, title, text)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_edit_post
            }
            _messageRes.postValue(errorMessageRes)
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
            _messageRes.postValue(errorMessageRes)
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
            _messageRes.postValue(errorMessageRes)
        }
    }

    class FeedViewModelFactory @Inject constructor(
        private val application: Application,
        private val postRepository: PostRepository,
        private val feedPagingSourceFactory: FeedPagingSourceFactory
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FeedViewModel(application, postRepository, feedPagingSourceFactory) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}