package ru.maxim.barybians.ui.dialog.editText

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.DialogEditTextBinding

class EditTextDialog(
    context: Context,
    private val title: String,
    private val text: String? = String(),
    private val onPositiveButtonClicked: (text: String) -> Unit,
    private val maxCharactersCount: Int? = null,
    private val hint: String? = null,
    private val isTextRequired: Boolean = true
) : MaterialAlertDialogBuilder(context) {

    private val editTextDialogBinding = DialogEditTextBinding.inflate(LayoutInflater.from(context)).apply {
        alertDialogEditText.setText(text)
        alertDialogEditText.hint = hint
        alertDialogEditText.setSelection(alertDialogEditText.length())
        alertDialogEditText.requestFocus()
        alertDialogEditText.addTextChangedListener { alertDialogEditTextLayout.error = null }

        alertDialogEditTextLayout.counterMaxLength = maxCharactersCount ?: 0
    }

    override fun create(): AlertDialog = MaterialAlertDialogBuilder(context).apply {
        setTitle(title)
        setView(editTextDialogBinding.root)
        setPositiveButton(R.string.ok, null)
        setNegativeButton(R.string.cancel, null)
    }.create()

    override fun show(): AlertDialog {
        val editTextDialog = create()
        editTextDialog.show()
        editTextDialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        editTextDialog.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
            val newText = editTextDialogBinding.alertDialogEditText.text.toString()
            if (newText.isNotBlank() || !isTextRequired) {
                if (text?.trim() == newText.trim()) {
                    editTextDialogBinding.alertDialogEditTextLayout.error =
                        context.getString(R.string.there_is_no_changes)
                } else if (maxCharactersCount != null && newText.length > maxCharactersCount) {
                    editTextDialogBinding.alertDialogEditTextLayout.error = context.getString(R.string.value_too_long)
                } else {
                    onPositiveButtonClicked(newText.trim())
                    editTextDialog.dismiss()
                }
            } else {
                editTextDialogBinding.alertDialogEditTextLayout.error =
                    context.getString(R.string.this_field_is_required)
            }
        }
        return editTextDialog
    }
}