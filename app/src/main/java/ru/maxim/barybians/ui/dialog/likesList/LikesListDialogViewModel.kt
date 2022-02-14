package ru.maxim.barybians.ui.dialog.likesList

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
import ru.maxim.barybians.data.network.exception.NotFoundException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.domain.model.User

class LikesListDialogViewModel private constructor(
    application: Application,
    private val postRepository: PostRepository,
    private val postId: Int
) : AndroidViewModel(application) {

    private val _likes: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val likes: StateFlow<List<User>> = _likes.asStateFlow()

    private val _errorMessageId: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessageId: LiveData<Int?> = _errorMessageId

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        update()
    }

    private fun update() = viewModelScope.launch {
        _isLoading.postValue(true)
        try {
            val post = postRepository.getPostById(postId)
            if (post != null) {
                _likes.emit(post.likedUsers)
            } else throw NotFoundException()
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


    class LikesListDialogViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val postRepository: PostRepository,
        @Assisted("postId") private val postId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LikesListDialogViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LikesListDialogViewModel(application, postRepository, postId) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("postId") postId: Int): LikesListDialogViewModelFactory
        }
    }
}