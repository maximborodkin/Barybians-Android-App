package ru.maxim.barybians.ui.dialog.likesList

import android.app.Application
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.like.LikeRepository
import ru.maxim.barybians.domain.model.User

class LikesListDialogViewModel private constructor(
    application: Application,
    likeRepository: LikeRepository,
    postId: Int
) : AndroidViewModel(application) {

    val likes: StateFlow<List<User>> =
        likeRepository.getLikes(postId)
            .catch { exception ->
                val errorMessage = when (exception) {
                    is NoConnectionException -> R.string.no_internet_connection
                    is TimeoutException -> R.string.request_timeout
                    else -> R.string.unable_to_load_likes
                }
                _errorMessage.postValue(errorMessage)
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _errorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = _errorMessage

    class LikesListDialogViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val likeRepository: LikeRepository,
        @Assisted("postId") private val postId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LikesListDialogViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LikesListDialogViewModel(application, likeRepository, postId) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("postId") postId: Int): LikesListDialogViewModelFactory
        }
    }
}