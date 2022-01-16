package ru.maxim.barybians.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentPostEditorBinding
import ru.maxim.barybians.databinding.FragmentPostMenuBinding

class PostMenuDialog : BottomSheetDialogFragment() {

    private var binding: FragmentPostMenuBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostMenuBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.fragmentPostMenuDelete?.setOnClickListener {

            AlertDialog.Builder(requireContext()).apply {
                setTitle(R.string.delete_this_post)
                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                setPositiveButton(R.string.ok) { dialog, _ ->
                    onDelete()
                    dialog.dismiss() // Dismiss AlertDialog
                    dismiss() // Dismiss BottomSheetDialog
                }
            }.show()
        }

        binding?.fragmentPostMenuEdit?.setOnClickListener {

            AlertDialog.Builder(requireContext()).apply {

                val editorDialogBinding = FragmentPostEditorBinding.inflate(layoutInflater)

                setView(editorDialogBinding.root)
                setTitle(R.string.edit_post)
                editorDialogBinding.fragmentPostEditorTitle.setText(title)
                editorDialogBinding.fragmentPostEditorText.setText(text)

                editorDialogBinding.fragmentPostEditorText.addTextChangedListener {
                    editorDialogBinding.fragmentPostEditorTextLayout.error = null
                }

                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

                setPositiveButton(R.string.ok) { dialog, _ ->
                    val newTitle = editorDialogBinding.fragmentPostEditorTitle.text.toString()
                    val newText = editorDialogBinding.fragmentPostEditorText.text.toString()
                    if (newText.isBlank()) {
                        editorDialogBinding.fragmentPostEditorTextLayout.error =
                            context.getString(R.string.this_field_is_required)
                    } else {
                        editorDialogBinding.fragmentPostEditorTextLayout.error = null
                        onEdit(newTitle, newText)
                        dialog.dismiss() // Dismiss AlertDialog
                        dismiss() // Dismiss BottomSheetDialog
                    }
                }
            }.show()
        }
    }

    companion object {
        private var title: String? = null
        private lateinit var text: String
        private lateinit var onDelete: () -> Unit
        private lateinit var onEdit: (title: String?, text: String) -> Unit

        fun newInstance(
            title: String?,
            text: String,
            onDelete: () -> Unit,
            onEdit: (title: String?, text: String) -> Unit
        ): PostMenuDialog {
            this.title = title
            this.text = text
            this.onDelete = onDelete
            this.onEdit = onEdit
            return PostMenuDialog()
        }
    }
}