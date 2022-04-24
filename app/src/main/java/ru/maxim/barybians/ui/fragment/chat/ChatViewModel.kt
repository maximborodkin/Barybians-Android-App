package ru.maxim.barybians.ui.fragment.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxim.barybians.data.repository.message.MessageRepository
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.User

class ChatViewModel private constructor(
    application: Application,
    userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val userId: Int
) : AndroidViewModel(application) {

    private val _errorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = _errorMessage

    private val _isSending: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSending: LiveData<Boolean> = _isSending

    val messageText: MutableLiveData<String> = MutableLiveData(String())

    val interlocutor: Flow<User?> = userRepository.getUserById(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val messages: StateFlow<PagingData<Message>> =
        messageRepository.getMessagesPager(userId)
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    val messagesCount: StateFlow<Int> = messageRepository.getMessagesCount(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun sendMessage() = viewModelScope.launch {
//        val message = messageText.value.toString()
//        if (message.isBlank()) return@launch
//        _isSending.postValue(true)
//        try {
//            val uuid = UUID.randomUUID().toString()
//            commentRepository.createComment(parseMode = ParseMode.MD, uuid = uuid, postId = postId, text = comment)
//            commentText.postValue(String())
//        } catch (e: Exception) {
//            Timber.e(e)
//            val error = when (e) {
//                is NoConnectionException -> R.string.no_internet_connection
//                is TimeoutException -> R.string.request_timeout
//                else -> R.string.unable_to_create_comment
//            }
//            _errorMessage.postValue(error)
//        } finally {
//            _isSending.postValue(false)
//        }
    }

    fun sendSticker(pack: String?, sticker: String?) = viewModelScope.launch {
//        if (pack.isNullOrBlank() || sticker.isNullOrBlank()) return@launch
//        _isSending.postValue(true)
//        try {
//            val uuid = UUID.randomUUID().toString()
//            val stickerMessage = "\$[$pack]($sticker)"
//            commentRepository.createComment(
//                parseMode = ParseMode.MD,
//                uuid = uuid,
//                postId = postId,
//                text = stickerMessage
//            )
//            commentText.postValue(String())
//        } catch (e: Exception) {
//            Timber.e(e)
//            val error = when (e) {
//                is NoConnectionException -> R.string.no_internet_connection
//                is TimeoutException -> R.string.request_timeout
//                else -> R.string.unable_to_create_comment
//            }
//            _errorMessage.postValue(error)
//        } finally {
//            _isSending.postValue(false)
//        }
    }

    class ChatViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val userRepository: UserRepository,
        private val messageRepository: MessageRepository,
        @Assisted("userId") private val userId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(application, userRepository, messageRepository, userId) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("userId") userId: Int): ChatViewModelFactory
        }
    }
}