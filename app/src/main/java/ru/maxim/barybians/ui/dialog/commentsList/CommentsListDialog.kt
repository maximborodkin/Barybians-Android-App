package ru.maxim.barybians.ui.dialog.commentsList

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentCommentsListBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.dialog.editText.EditTextDialog
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class CommentsListDialog : BottomSheetDialogFragment(), CommentsAdapterListener {

    private val args: CommentsListDialogArgs by navArgs()
    private var binding: FragmentCommentsListBinding by notNull()

    @Inject
    lateinit var commentsRecyclerAdapter: CommentsListRecyclerAdapter

    @Inject
    lateinit var factory: CommentsListViewModel.CommentsListViewModelFactory.Factory
    private val model: CommentsListViewModel by viewModels { factory.create(args.postId) }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
            commentsRecyclerAdapter.setAdapterListener(this@CommentsListDialog)
            commentsListRecycler.adapter = commentsRecyclerAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.comments.collect(commentsRecyclerAdapter::submitList)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessage.observe(viewLifecycleOwner) { messageResource -> context?.toast(messageResource) }
            model.isSending.observe(viewLifecycleOwner) { isSending ->
                if (isSending) {
                    val sendingIcon = AppCompatResources.getDrawable(view.context, R.drawable.ic_timer_animated)
                    commentsListSendBtn.setImageDrawable(sendingIcon)
                    (sendingIcon as? Animatable)?.start()
                } else {
                    commentsListSendBtn.setImageResource(R.drawable.ic_send)
                }
            }
        }

        commentsListSendBtn.setOnClickListener { model.createComment() }
        commentsListSortingDirectionButton.setOnClickListener {
            model.sortingDirection.postValue(model.sortingDirection.value?.not())
        }
    }

    override fun onUserClick(userId: Int) = findNavController().navigate(CommentsListDialogDirections.toProfile(userId))

    override fun onImageClick(url: String) {
        findNavController().navigate(CommentsListDialogDirections.toImageViewer(imageUrl = url))
    }

    override fun onCommentMenuClick(comment: Comment, anchor: View) {
        val activity = activity ?: return
        PopupMenu(activity, anchor).apply {
            activity.menuInflater.inflate(R.menu.menu_comment, menu)

            menu.findItem(R.id.menuCommentEdit)?.setOnMenuItemClickListener {
                showEditDialog(comment.text) { text -> model.editComment(commentId = comment.commentId, text = text) }
                true
            }

            menu.findItem(R.id.menuCommentDelete)?.setOnMenuItemClickListener {
                showDeleteDialog { model.deleteComment(commentId = comment.commentId) }
                true
            }
            show()
        }
    }

    private fun showEditDialog(text: String, onEdit: (text: String) -> Unit) {
        EditTextDialog(
            context = context ?: return,
            title = getString(R.string.edit_comment),
            text = text,
            onPositiveButtonClicked = onEdit,
            maxCharactersCount = 2000
        ).show()
    }

    private fun showDeleteDialog(onDelete: () -> Unit) {
        MaterialAlertDialogBuilder(context ?: return).apply {
            setTitle(R.string.delete_this_comment)
            setIcon(R.drawable.ic_delete)
            setMessage(R.string.this_action_cannot_be_undone)
            setNegativeButton(android.R.string.cancel, null)
            setPositiveButton(android.R.string.ok) { _, _ -> onDelete() }
        }.show()
    }


    override fun onDestroyView() {
        commentsRecyclerAdapter.setAdapterListener(null)
        super.onDestroyView()
    }
}