package ru.maxim.barybians.ui.dialog.commentsList

import android.app.Application
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.CommentRepository
import ru.maxim.barybians.domain.model.Comment
import java.util.*

class CommentsListDialogViewModel private constructor(
    application: Application,
    private val commentRepository: CommentRepository,
    private val postId: Int
) : AndroidViewModel(application) {

    private val _comments: MutableStateFlow<List<Comment>> = MutableStateFlow(listOf())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _errorMessageId: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessageId: LiveData<Int?> = _errorMessageId

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSending: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSending: LiveData<Boolean> = _isSending

    val commentText: MutableLiveData<String> = MutableLiveData(String())

    init {
        update()
    }

    private fun update() = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            _comments.emit(commentRepository.getComments(postId))
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_load_comments
            }
            _errorMessageId.postValue(error)
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun editComment(commentId: Int, text: String) = viewModelScope.launch {
        _isSending.postValue(true)
        try {
            commentRepository.editComment(commentId, text)
            update()
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_update_comment
            }
            _errorMessageId.postValue(error)
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
            update()
            commentText.postValue(String())
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_create_comment
            }
            _errorMessageId.postValue(error)
        } finally {
            _isSending.postValue(false)
        }
    }


    fun deleteComment(commentId: Int) = viewModelScope.launch {
        try {
            commentRepository.deleteComment(commentId)
            val updatedComments = comments.value.filterNot { it.id == commentId }
            _comments.emit(updatedComments)
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_delete_comment
            }
            _errorMessageId.postValue(error)
        }
    }

    class CommentsListDialogViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val commentRepository: CommentRepository,
        @Assisted("postId") private val postId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommentsListDialogViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CommentsListDialogViewModel(application, commentRepository, postId) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("postId") postId: Int): CommentsListDialogViewModelFactory
        }
    }
}