package ru.maxim.barybians.ui.dialog.editText

import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.DialogEdittextBinding

class EditTextDialog(
    context: Context,
    title: String,
    text: String? = null,
    onPositiveButtonClicked: (title: String) -> Unit,
    maxCharactersCount: Int? = null,
    hint: String? = null
) : MaterialAlertDialogBuilder(context) {

    init {
        val customView = DialogEdittextBinding.inflate(LayoutInflater.from(context))
        setView(customView.root)

        setTitle(title)

        with(customView.alertDialogEditText) {
            setText(text)
            setHint(hint)
            setSelection(length())
            requestFocus()
            addTextChangedListener { customView.alertDialogEditTextLayout.error = null }
        }
        maxCharactersCount?.let { customView.alertDialogEditTextLayout.counterMaxLength = it }

        setPositiveButton(android.R.string.ok, null)
        setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }

        val dialog = create()
        dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newText = customView.alertDialogEditText.text.toString()
            if (newText.isNotBlank()) {
                if (maxCharactersCount != null && newText.length > maxCharactersCount) {
                    customView.alertDialogEditTextLayout.error =
                        context.getString(R.string.string_too_long)
                } else {
                    onPositiveButtonClicked(newText)
                    dialog.dismiss()
                }
            } else {
                customView.alertDialogEditTextLayout.error =
                    context.getString(R.string.this_string_is_required)
            }
        }
    }
}