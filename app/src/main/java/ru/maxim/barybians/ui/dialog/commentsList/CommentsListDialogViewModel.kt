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

    init {
        update()
    }

    private fun update() = viewModelScope.launch {
        try {
            _comments.emit(commentRepository.getComments(postId))
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_load_comments
            }
            _errorMessageId.postValue(error)
        }
    }

    fun editComment(commentId: Int, text: String) = viewModelScope.launch {
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
        }
    }

    fun createComment(text: String) = viewModelScope.launch {
        try {
            val uuid = UUID.randomUUID().toString()
            commentRepository.createComment(uuid, postId, text)
        } catch (e: Exception) {
            val error = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_create_comment
            }
            _errorMessageId.postValue(error)
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