package ru.maxim.barybians.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_comments_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_likes_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_post_editor.view.*
import kotlinx.android.synthetic.main.fragment_post_menu_bottom_sheet.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.fragment.profile.CommentsRecyclerAdapter
import ru.maxim.barybians.ui.fragment.profile.LikedUsersRecyclerAdapter
import ru.maxim.barybians.ui.fragment.profile.OnImageClickListener
import ru.maxim.barybians.ui.fragment.profile.OnUserClickListener
import ru.maxim.barybians.ui.fragment.profile.ProfileItemPost.ItemComment
import ru.maxim.barybians.ui.fragment.profile.ProfileItemPost.ItemUser
import ru.maxim.barybians.utils.HtmlParser

/**
 * Singleton class for create dialogs
 */
object DialogFactory {

    fun createLikesListDialog(
        context: Context,
        likes: ArrayList<ItemUser>,
        onUserClickListener: OnUserClickListener
    ) =
        BottomSheetDialog(context).apply {
            setContentView(R.layout.fragment_likes_bottom_sheet)
            val likesCount = likes.size
            if (likesCount == 0) {
                likesBottomSheetTitle.visibility = View.GONE
                likesBottomSheetMessage.text = context.getString(R.string.nobody_like_this)
            } else {
                likesBottomSheetMessage.visibility = View.GONE
                likesBottomSheetTitle.text =
                    context.resources.getQuantityString(R.plurals.like_plurals, likesCount, likesCount)
            }
            likesBottomSheetRecyclerView.let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = LikedUsersRecyclerAdapter(likes, onUserClickListener)
            }
        }

    fun createCommentsListDialog(context: Context,
                                 comments: ArrayList<ItemComment>,
                                 onUserClickListener: OnUserClickListener,
                                 onImageClickListener: OnImageClickListener,
                                 htmlParser: HtmlParser,
                                 addCommentCallback: (text: String) -> Unit,
                                 deleteCommentCallback: (commentsCount: Int, commentPosition: Int, commentId: Int) -> Unit) =
        BottomSheetDialog(context).apply {
            setContentView(R.layout.fragment_comments_bottom_sheet)
            val commentsCount = comments.size
            if (commentsCount == 0) {
                commentsBottomSheetTitle.visibility = View.GONE
                commentsBottomSheetMessage.text = context.getString(R.string.no_comments_yet)
            } else {
                commentsBottomSheetMessage.visibility = View.GONE
                commentsBottomSheetTitle.text =
                    context.resources.getQuantityString(R.plurals.comment_plurals, commentsCount, commentsCount)
            }
            commentsBottomSheetRecyclerView.let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = CommentsRecyclerAdapter(comments, onUserClickListener,
                    onImageClickListener, deleteCommentCallback, htmlParser)
            }

            commentsBottomSheetEditor.addTextChangedListener {
                val buttonResource =
                    if (it.isNullOrBlank()) R.drawable.ic_send_grey
                    else R.drawable.ic_send_blue
                commentsBottomSheetSend.setBackgroundResource(buttonResource)
                commentsBottomSheetEditor.requestFocus()
            }
            commentsBottomSheetSend.apply {
                setBackgroundResource(R.drawable.ic_send_grey)
                setOnClickListener {
                    val text = commentsBottomSheetEditor.text
                    if (!text.isNullOrBlank()) {
                        addCommentCallback(text.toString())
                    }
                }
            }
        }

    fun createEditStatusDialog(context: Context,
                               status: String?,
                               editCallback: (status: String?) -> Unit) =
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.edit_status))
            val editText = EditText(context).apply {
                setText(status)
                hint = context.getString(R.string.new_status)
            }
            if (editText.parent == null) { setView(editText) }
            setPositiveButton(R.string.ok) { dialog, _ ->
                editCallback(editText.text.toString())
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
        }.create()


    fun createPostMenu(context: Context,
                       title: String?,
                       text: String,
                       deleteCallback: () -> Unit,
                       editCallback: (title: String?, text: String) -> Unit): BottomSheetDialog =
         BottomSheetDialog(context).apply {
            setContentView(R.layout.fragment_post_menu_bottom_sheet)

            fragmentPostMenuBottomSheetDelete.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.delete_this_post)
                    setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    setPositiveButton(R.string.ok) { dialog, _ ->
                        deleteCallback()
                        dialog.dismiss() // Dismiss AlertDialog
                        dismiss() // Dismiss BottomSheetDialog
                    }
                }.show()
            }

            fragmentPostMenuBottomSheetEdit.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.fragment_post_editor, null, false)
                    setView(view)
                    setTitle(R.string.edit_post)
                    view.fragmentPostEditorTitle.setText(title)
                    view.fragmentPostEditorText.setText(text)

                    view.fragmentPostEditorText.addTextChangedListener {
                        if (it.toString().isNotBlank()) {
                            view.fragmentPostEditorTextLayout.error = null
                        }
                    }

                    setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

                    setPositiveButton(R.string.ok) { dialog, _ ->
                        val newTitle = view.fragmentPostEditorTitle.text.toString()
                        val newText = view.fragmentPostEditorText.text.toString()
                        if (text.isBlank()) {
                            view.fragmentPostEditorTextLayout.error =
                                context.getString(R.string.this_field_is_required)
                        } else {
                            view.fragmentPostEditorTextLayout.error = null
                            editCallback(newTitle, newText)
                            dialog.dismiss() // Dismiss AlertDialog
                            dismiss() // Dismiss BottomSheetDialog
                        }
                    }
                }.show()
            }
        }

}