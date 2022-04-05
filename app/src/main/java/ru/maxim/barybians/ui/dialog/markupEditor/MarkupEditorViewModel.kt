package ru.maxim.barybians.ui.dialog.markupEditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.maxim.barybians.domain.model.Attachment

@OptIn(ExperimentalPagingApi::class)
class MarkupEditorViewModel private constructor(
    application: Application,
    initialText: String,
    initialAttachments: List<Attachment>
) : AndroidViewModel(application) {

    val text: MutableLiveData<String> = MutableLiveData(initialText)
    val attachments: MutableLiveData<List<Attachment>> = MutableLiveData(initialAttachments)

    fun addAttachment(attachment: Attachment) {
        val newValue = listOf(*attachments.value?.toTypedArray() ?: emptyArray(), attachment)
        attachments.postValue(newValue)
    }

    fun removeAttachment(attachment: Attachment) {
        val newValue = attachments.value?.toMutableList()?.apply { remove(attachment) } ?: emptyList()
        attachments.postValue(newValue)
    }

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