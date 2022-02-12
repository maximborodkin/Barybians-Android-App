package ru.maxim.barybians.ui.dialog.commentsList

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentCommentsListBinding
import ru.maxim.barybians.ui.dialog.editText.EditTextDialog
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.hide
import ru.maxim.barybians.utils.show
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
                model.comments.collect(commentsRecyclerAdapter::submitData)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentsRecyclerAdapter.loadStateFlow.collect { state ->
                    when (state.refresh) {
                        is LoadState.Loading -> {
                            commentsListProgressBar.isVisible = commentsRecyclerAdapter.itemCount == 0
                            commentsListMessage.hide()
                        }
                        is LoadState.NotLoading -> {
                            commentsListProgressBar.hide()
                            if (commentsRecyclerAdapter.itemCount > 0) commentsListMessage.hide()
                        }
                        is LoadState.Error -> {
                            commentsListProgressBar.hide()
                            val errorMessage = getString(R.string.an_error_occurred_while_loading_comments)
                            if (commentsRecyclerAdapter.itemCount == 0) {
                                commentsListMessage.show()
                                commentsListMessage.text = errorMessage
                            } else {
                                context?.toast(errorMessage)
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.messageRes.observe(viewLifecycleOwner) { messageRes ->
                messageRes?.let { context?.toast(messageRes) }
            }
            model.isSending.observe(viewLifecycleOwner) { isSending ->
                if (isSending) {
                    val timerAnimatedDrawable =
                        AppCompatResources.getDrawable(view.context, R.drawable.ic_timer_animated)
                    commentsListSendBtn.setImageDrawable(timerAnimatedDrawable)
                    (timerAnimatedDrawable as? Animatable)?.start()
                } else {
                    commentsListSendBtn.setImageResource(R.drawable.ic_send)
                }
            }
        }

        commentsListSendBtn.setOnClickListener { model.createComment() }
    }

    override fun onUserClick(userId: Int) = findNavController().navigate(CommentsListDialogDirections.toProfile(userId))

    override fun onImageClick(url: String) {
        findNavController().navigate(CommentsListDialogDirections.toImageViewer(imageUrl = url))
    }

    override fun onCommentSwipe(commentId: Int, viewHolderPosition: Int) {
        val deleteConfirmationDialog = MaterialAlertDialogBuilder(context ?: return).apply {
            setTitle(R.string.delete_this_comment)
            setIcon(R.drawable.ic_delete_grey)
            setMessage(R.string.this_action_cannot_be_undone)
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                commentsRecyclerAdapter.notifyItemChanged(viewHolderPosition)
                dialog.dismiss()
            }
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                model.deleteComment(commentId)
                dialog.dismiss()
            }
        }
        deleteConfirmationDialog.setOnCancelListener {
            commentsRecyclerAdapter.notifyItemChanged(viewHolderPosition)
        }
        deleteConfirmationDialog.show()
    }

    override fun onCommentLongClick(commentId: Int, commentText: String) {
        EditTextDialog(
            context = context ?: return,
            title = getString(R.string.edit_comment),
            text = commentText,
            onPositiveButtonClicked = { newText ->
                model.editComment(commentId, newText)
            }
        )
    }

    override fun onDestroyView() {
        commentsRecyclerAdapter.setAdapterListener(null)
        super.onDestroyView()
    }
}