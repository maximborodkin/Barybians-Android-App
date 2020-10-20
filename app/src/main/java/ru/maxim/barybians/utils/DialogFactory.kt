package ru.maxim.barybians.utils

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_comments_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_likes_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_post_editor.view.*
import kotlinx.android.synthetic.main.fragment_post_menu_bottom_sheet.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity
import ru.maxim.barybians.ui.fragment.base.PostItem.CommentItem
import ru.maxim.barybians.ui.fragment.base.PostItem.UserItem
import ru.maxim.barybians.ui.fragment.feed.CommentsRecyclerAdapter
import ru.maxim.barybians.ui.fragment.feed.LikedUsersRecyclerAdapter

/**
 * Singleton class for create dialogs
 */
object DialogFactory {

    fun createLikesListDialog(
        likes: ArrayList<UserItem>,
        onUserClick: (userId: Int) -> Unit
    ) = LikesBottomSheetFragment.newInstance(likes, onUserClick)

    fun createEditStatusDialog(
        context: Context,
        status: String? = null,
        onStatusEdited: (status: String?) -> Unit,
        onStatusEditConfirmed: () -> Unit
    ) = MaterialAlertDialogBuilder(context).apply {
            setTitle(context.getString(R.string.edit_status))
            val container = FrameLayout(context)
            val margin = dpToPx(context.resources, 16)
            val containerLayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = margin; marginEnd = margin}


            val editText = EditText(context).apply {
                setText(status)
                hint = context.getString(R.string.new_status)
                setSingleLine()
                setHintTextColor(ContextCompat.getColor(context, R.color.dark_colorTertiaryText))
                addTextChangedListener { onStatusEdited(text.toString()) }
                layoutParams = containerLayoutParams
            }
            container.addView(editText)
            if (container.parent == null) { setView(container) }
                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                setPositiveButton(R.string.ok) { dialog, _ ->
                    onStatusEditConfirmed()
                    dialog.dismiss()
                }
        }.create()

    fun createPostMenu(
        title: String?,
        text: String,
        onDelete: () -> Unit,
        onEdit: (title: String?, text: String) -> Unit
    ) = PostMenuBottomSheetFragment.newInstance(title, text, onDelete, onEdit)


    class LikesBottomSheetFragment : BottomSheetDialogFragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.fragment_likes_bottom_sheet, container, false)

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val likesCount = likes.size
            if (likesCount == 0) {
                likesBottomSheetTitle.visibility = View.GONE
                likesBottomSheetMessage.text = context?.getString(R.string.nobody_like_this)
            } else {
                likesBottomSheetMessage.visibility = View.GONE
                likesBottomSheetTitle.text =
                    context?.resources?.getQuantityString(
                        R.plurals.like_plurals,
                        likesCount,
                        likesCount
                    )
            }
            likesBottomSheetRecyclerView.let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = LikedUsersRecyclerAdapter(likes) { userId ->
                    onUserClick(userId)
                    dismiss()
                }
            }
        }

        companion object {
            private lateinit var likes: ArrayList<UserItem>
            private lateinit var onUserClick: (userId: Int) -> Unit

            fun newInstance(
                likes: ArrayList<UserItem>,
                onUserClick: (userId: Int) -> Unit
            ): LikesBottomSheetFragment {
                this.likes = likes
                this.onUserClick = onUserClick
                return LikesBottomSheetFragment()
            }
        }
    }

    class PostMenuBottomSheetFragment : BottomSheetDialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.fragment_post_menu_bottom_sheet, container, false)

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            fragmentPostMenuBottomSheetDelete.setOnClickListener {
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

            fragmentPostMenuBottomSheetEdit.setOnClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    val dialogView = LayoutInflater.from(context)
                        .inflate(R.layout.fragment_post_editor, null, false)
                    setView(dialogView)
                    setTitle(R.string.edit_post)
                    dialogView.fragmentPostEditorTitle.setText(title)
                    dialogView.fragmentPostEditorText.setText(text)

                    dialogView.fragmentPostEditorText.addTextChangedListener {
                        if (it.toString().isNotBlank()) {
                            dialogView.fragmentPostEditorTextLayout.error = null
                        }
                    }

                    setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

                    setPositiveButton(R.string.ok) { dialog, _ ->
                        val newTitle = dialogView.fragmentPostEditorTitle.text.toString()
                        val newText = dialogView.fragmentPostEditorText.text.toString()
                        if (text.isBlank()) {
                            dialogView.fragmentPostEditorTextLayout.error =
                                context.getString(R.string.this_field_is_required)
                        } else {
                            dialogView.fragmentPostEditorTextLayout.error = null
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
            ): PostMenuBottomSheetFragment {
                this.title = title
                this.text = text
                this.onDelete = onDelete
                this.onEdit = onEdit
                return PostMenuBottomSheetFragment()
            }
        }
    }

    fun createCommentsListDialog(context: Context,
                                 comments: ArrayList<CommentItem>,
                                 htmlParser: HtmlParser,
                                 onUserClick: (userId: Int) -> Unit,
                                 onImageClick: (drawable: Drawable) -> Unit,
                                 onCommentAdd: (text: String) -> Unit,
                                 onCommentDelete: (commentPosition: Int, commentId: Int) -> Unit) =
        BottomSheetDialog(context).apply {
            setContentView(R.layout.fragment_comments_bottom_sheet)
            val commentsCount = comments.size

            commentsBottomSheetTitle.text = if (commentsCount == 0) {
                 context.getString(R.string.no_comments_yet)
            } else {
                context.resources.getQuantityString(
                    R.plurals.comment_plurals,
                    commentsCount,
                    commentsCount
                )
            }
            commentsBottomSheetRecyclerView.let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = CommentsRecyclerAdapter(
                    comments,
                    onUserClick,
                    onImageClick,
                    onCommentDelete,
                    htmlParser
                )
            }

            commentsBottomSheetEditor.addTextChangedListener {
                val buttonTintResource =
                    if (it.isNullOrBlank()) R.color.send_btn_disabled_color
                    else R.color.send_btn_enabled_color
                commentsBottomSheetSend.setColorFilter(buttonTintResource)
                commentsBottomSheetEditor.requestFocus()
            }
            commentsBottomSheetSend.apply {
                setBackgroundResource(R.drawable.ic_send)
                setOnClickListener {
                    val text = commentsBottomSheetEditor.text
                    if (!text.isNullOrBlank()) {
                        onCommentAdd(text.toString())
                    }
                }
            }
        }

    fun createLogoutAlertDialog() = object : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?) =
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(getString(R.string.are_you_sure))
                setPositiveButton(R.string.yes) { _, _ ->
                    PreferencesManager.token = null
                    PreferencesManager.userId = 0
                    val loginActivityIntent = Intent(context, LoginActivity::class.java)
                    loginActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginActivityIntent)
                }
                setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            }.create()
    }
}
