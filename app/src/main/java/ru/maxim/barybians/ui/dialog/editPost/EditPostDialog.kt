package ru.maxim.barybians.ui.dialog.editPost

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentPostEditorBinding

class EditPostDialog(
    context: Context,
    @StringRes private val dialogTitle: Int,
    private val title: String?,
    private val text: String = String(),
    private val onPositiveButtonClicked: (title: String?, text: String) -> Unit
) : MaterialAlertDialogBuilder(context) {

    private val editPostDialogBinding = FragmentPostEditorBinding.inflate(LayoutInflater.from(context)).apply {
        fragmentPostEditorTitle.setText(title)
        fragmentPostEditorText.setText(text)
        fragmentPostEditorTitleLayout.counterMaxLength = titleMaxLength
        fragmentPostEditorTextLayout.counterMaxLength = textMaxLength
        fragmentPostEditorTitle.addTextChangedListener { fragmentPostEditorTitleLayout.error = null }
        fragmentPostEditorText.addTextChangedListener { fragmentPostEditorTextLayout.error = null }
    }

    override fun create(): AlertDialog = MaterialAlertDialogBuilder(context).apply {
        setTitle(dialogTitle)
        setView(editPostDialogBinding.root)
        setPositiveButton(R.string.ok, null)
        setNegativeButton(R.string.cancel, null)
    }.create()

    override fun show(): AlertDialog {
        val editPostDialog = create()
        editPostDialog.setOnShowListener { dialog ->
            (dialog as? AlertDialog)?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                with(editPostDialogBinding) {
                    val newTitle = fragmentPostEditorTitle.text.toString()
                    val newText = fragmentPostEditorText.text.toString()
                    if (newText.isBlank()) {
                        fragmentPostEditorTextLayout.error = context.getString(R.string.this_field_is_required)
                    } else if (newTitle.length > titleMaxLength) {
                        fragmentPostEditorTitleLayout.error = context.getString(R.string.title_length_error)
                    } else if (newText.length > textMaxLength) {
                        fragmentPostEditorTextLayout.error = context.getString(R.string.text_length_error)
                    } else if (title == newTitle && text == newText) {
                        fragmentPostEditorTextLayout.error = context.getString(R.string.there_is_no_changes)
                        fragmentPostEditorTitleLayout.error = context.getString(R.string.there_is_no_changes)
                    } else {
                        onPositiveButtonClicked(newTitle, newText)
                        dialog.dismiss()
                    }
                }
            }
        }
        editPostDialog.show()
        return editPostDialog
    }

    private companion object {
        private const val titleMaxLength = 50
        private const val textMaxLength = 4000
    }
}