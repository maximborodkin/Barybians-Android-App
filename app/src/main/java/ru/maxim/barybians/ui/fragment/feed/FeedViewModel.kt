package ru.maxim.barybians.ui.fragment.feed

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.paging.FeedRemoteMediator
import ru.maxim.barybians.data.paging.FeedRemoteMediator.FeedRemoteMediatorFactory
import ru.maxim.barybians.data.repository.like.LikeRepository
import ru.maxim.barybians.data.repository.post.PostRepository
import ru.maxim.barybians.domain.model.Post
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
open class FeedViewModel constructor(
    application: Application,
    feedRemoteMediator: FeedRemoteMediator,
    protected val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    private val postEntityMapper: PostEntityMapper
) : AndroidViewModel(application) {

    // When your wings are burning, who keeps you from falling?
    open val postsList: StateFlow<PagingData<Post>> = Pager(
        config = PagingConfig(
            initialLoadSize = PostRepository.pageSize,
            pageSize = PostRepository.pageSize,
            prefetchDistance = PostRepository.prefetchDistance,
            enablePlaceholders = true
        ),
        remoteMediator = feedRemoteMediator,
        pagingSourceFactory = { postRepository.feedPagingSource() }
    )
        .flow
        .map { pagingData ->
            postsCount = 0
            pagingData.map { entityModel ->
                postsCount++
                postEntityMapper.toDomainModel(entityModel)
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    protected val mErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = mErrorMessage

    var postsCount = 0
        private set

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
        private val postEntityMapper: PostEntityMapper,
        private val likeRepository: LikeRepository,
        private val remoteMediatorFactory: FeedRemoteMediatorFactory,
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                return FeedViewModel(
                    application,
                    remoteMediatorFactory.create(),
                    postRepository,
                    likeRepository,
                    postEntityMapper
                ) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}