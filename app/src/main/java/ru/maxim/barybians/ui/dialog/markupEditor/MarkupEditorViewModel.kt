package ru.maxim.barybians.ui.dialog.markupEditor

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
import ru.maxim.barybians.data.network.model.ParseMode
import ru.maxim.barybians.data.repository.comment.CommentRepository
import ru.maxim.barybians.domain.model.Attachment
import ru.maxim.barybians.domain.model.Comment
import timber.log.Timber
import java.util.*

@OptIn(ExperimentalPagingApi::class)
class MarkupEditorViewModel private constructor(
    application: Application,
    private val initialText: String,
    private val initialAttachments: List<Attachment>
) : AndroidViewModel(application) {

    val text: MutableLiveData<String> = MutableLiveData(initialText)
    val attachments: MutableLiveData<List<Attachment>> = MutableLiveData(initialAttachments)

    class MarkupEditorViewModelFactory @AssistedInject constructor(
        private val application: Application,
        @Assisted("text") private val text: String,
        @Assisted("attachments") private val attachments: List<Attachment>
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MarkupEditorViewModel::class.java)) {
                return MarkupEditorViewModel(application, text, attachments) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(
                @Assisted("text") text: String,
                @Assisted("attachments") attachments: List<Attachment>
            ): MarkupEditorViewModelFactory
        }
    }
}