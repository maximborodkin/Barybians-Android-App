package ru.maxim.barybians.ui.dialog.stickerPicker

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.sticker.StickerPackRepository
import ru.maxim.barybians.domain.model.StickerPack
import javax.inject.Inject

class StickerPickerViewModel private constructor(
    application: Application,
    private val stickerPackRepository: StickerPackRepository,
) : AndroidViewModel(application) {

    suspend fun getStickers(): StateFlow<List<StickerPack>> =
        stickerPackRepository.getStickerPacks()
            .catch { exception ->
                val errorMessage = when (exception) {
                    is NoConnectionException -> R.string.no_internet_connection
                    is TimeoutException -> R.string.request_timeout
                    else -> R.string.unable_to_load_stickers
                }
                _errorMessage.postValue(errorMessage)
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _errorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = _errorMessage

    class StickerPickerViewModelFactory @Inject constructor(
        private val application: Application,
        private val stickerPackRepository: StickerPackRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StickerPickerViewModel::class.java)) {
                return StickerPickerViewModel(application, stickerPackRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}