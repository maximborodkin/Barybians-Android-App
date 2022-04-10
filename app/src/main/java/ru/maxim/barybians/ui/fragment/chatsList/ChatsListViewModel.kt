package ru.maxim.barybians.ui.fragment.chatsList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.chat.ChatRepository
import ru.maxim.barybians.domain.model.Chat
import javax.inject.Inject

class ChatsListViewModel private constructor(
    application: Application,
    private val chatsRepository: ChatRepository,
) : AndroidViewModel(application) {

    val chats: StateFlow<List<Chat>> =
        chatsRepository.getChatsList()
            .catch { exception ->
                val errorMessage = when (exception) {
                    is NoConnectionException -> R.string.no_internet_connection
                    is TimeoutException -> R.string.request_timeout
                    else -> R.string.unable_to_load_chats
                }
                _errorMessage.postValue(errorMessage)
                _isLoading.value = false
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = _errorMessage

    fun refresh() = viewModelScope.launch(IO) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        chatsRepository.refreshChatsList()
        _isLoading.postValue(false)
    }

    class ChatsListViewModelFactory @Inject constructor(
        private val application: Application,
        private val chatRepository: ChatRepository,
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatsListViewModel::class.java)) {
                return ChatsListViewModel(application, chatRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}