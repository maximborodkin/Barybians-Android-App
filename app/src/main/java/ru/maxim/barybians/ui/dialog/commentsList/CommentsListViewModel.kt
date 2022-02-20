package ru.maxim.barybians.ui.dialog.commentsList

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.comment.CommentRepository
import ru.maxim.barybians.domain.model.Comment
import timber.log.Timber
import java.util.*

@OptIn(ExperimentalPagingApi::class)
class CommentsListViewModel private constructor(
    application: Application,
    private val commentRepository: CommentRepository,
    private val postId: Int
) : AndroidViewModel(application) {

    private val _messageRes: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = _messageRes

    private val _isSending: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSending: LiveData<Boolean> = _isSending

    val commentText: MutableLiveData<String> = MutableLiveData(String())
    val sortingDirection: MutableLiveData<Boolean> = MutableLiveData(true) // false - descending, true - ascending

    // Maxutka love
    // kotek rulez
    val comments: StateFlow<List<Comment>> = Transformations.switchMap(sortingDirection) { direction ->
        commentRepository.getComments(postId = postId, sortingDirection = direction)
    }
        .asFlow()
        .catch { exception ->
            val errorMessage = when (exception) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_load_comments
            }
            _messageRes.postValue(errorMessage)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun editComment(commentId: Int, text: String) = viewModelScope.launch {
        _isSending.postValue(true)
        try {
            commentRepository.editComment(commentId, text)
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_edit_comment
            }
            _messageRes.postValue(error)
        } finally {
            _isSending.postValue(false)
        }
    }

    fun createComment() = viewModelScope.launch {
        if (commentText.value.isNullOrBlank()) return@launch
        _isSending.postValue(true)
        try {
            val uuid = UUID.randomUUID().toString()
            commentRepository.createComment(uuid, postId, requireNotNull(commentText.value))
            commentText.postValue(String())
        } catch (e: Exception) {
            Timber.e(e)
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_create_comment
            }
            _messageRes.postValue(error)
        } finally {
            _isSending.postValue(false)
        }
    }


    fun deleteComment(commentId: Int) = viewModelScope.launch {
        try {
            commentRepository.deleteComment(commentId)
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_delete_comment
            }
            _messageRes.postValue(error)
        }
    }

    class CommentsListViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val commentRepository: CommentRepository,
        @Assisted("postId") private val postId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommentsListViewModel::class.java)) {
                return CommentsListViewModel(application, commentRepository, postId) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("postId") postId: Int): CommentsListViewModelFactory
        }
    }
}