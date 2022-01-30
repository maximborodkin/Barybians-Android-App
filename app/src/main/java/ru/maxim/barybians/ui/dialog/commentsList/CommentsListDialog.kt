package ru.maxim.barybians.ui.dialog.commentsList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
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
import ru.maxim.barybians.ui.dialog.editText.EditTextDialog
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class CommentsListDialog : BottomSheetDialogFragment() {

    private val args: CommentsListDialogArgs by navArgs()
    private var binding: FragmentCommentsListBinding by notNull()

    @Inject
    lateinit var dateFormatUtils: DateFormatUtils

    @Inject
    lateinit var recyclerAdapter: CommentsListRecyclerAdapter

    @Inject
    lateinit var factory: CommentsListDialogViewModel.CommentsListDialogViewModelFactory.Factory
    private val model: CommentsListDialogViewModel by viewModels { factory.create(args.postId) }

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
            commentsListRecycler.adapter = recyclerAdapter
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.comments.collect { comments ->
                    recyclerAdapter.submitList(comments)

                    binding.commentsListMessage.text =
                        if (comments.isEmpty()) view.context.getString(R.string.no_comments_yet)
                        else null

                    binding.commentsListTitle.text =
                        view.context.resources.getQuantityString(
                            R.plurals.comment_plurals,
                            comments.size,
                            comments.size
                        )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessageId.observe(viewLifecycleOwner) { errorMessageId ->
                errorMessageId?.let {
                    if (recyclerAdapter.itemCount == 0) {
                        binding.commentsListMessage.text = view.context.getText(errorMessageId)
                    } else {
                        recyclerAdapter.notifyDataSetChanged()
                        context?.toast(it)
                    }
                }
            }
            model.isSending.observe(viewLifecycleOwner) { isSending ->
                if (isSending) {
                    val timerAnimatedDrawable =
                        AppCompatResources.getDrawable(view.context, R.drawable.ic_timer_animated)
                    binding.commentsListSendBtn.setImageDrawable(timerAnimatedDrawable)
                    (timerAnimatedDrawable as? Animatable)?.start()
                } else {
                    binding.commentsListSendBtn.setImageResource(R.drawable.ic_send)
                }
            }
        }

        recyclerAdapter.apply {
            setOnUserClickListener { userId ->
                val action = CommentsListDialogDirections.toProfile(userId)
                findNavController().navigate(action)
            }
            setOnImageClickListener { imageDrawable ->
                val action = CommentsListDialogDirections.toImageViewer(imageBitmap = imageDrawable.toBitmap())
                findNavController().navigate(action)
            }
            setOnCommentSwipeListener { commentId, viewHolderPosition ->
                val deleteConfirmationDialog = MaterialAlertDialogBuilder(view.context).apply {
                    setTitle(R.string.delete_this_comment)
                    setIcon(R.drawable.ic_delete_grey)
                    setMessage(R.string.this_action_cannot_be_undone)
                    setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        recyclerAdapter.notifyItemChanged(viewHolderPosition)
                        dialog.dismiss()
                    }
                    setPositiveButton(android.R.string.ok) { dialog, _ ->
                        model.deleteComment(commentId)
                        dialog.dismiss()
                    }
                }
                deleteConfirmationDialog.setOnCancelListener {
                    recyclerAdapter.notifyItemChanged(viewHolderPosition)
                }
                deleteConfirmationDialog.show()
            }
            setOnCommentLongClickListener { commentId, commentText ->
                EditTextDialog(
                    context = view.context,
                    title = getString(R.string.edit_comment),
                    text = commentText,
                    onPositiveButtonClicked = { newText ->
                        model.editComment(commentId, newText)
                    }
                )
            }
        }

        binding.commentsListSendBtn.setOnClickListener {
            model.createComment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter.setOnUserClickListener(null)
        recyclerAdapter.setOnImageClickListener(null)
        recyclerAdapter.setOnCommentSwipeListener(null)
        recyclerAdapter.setOnCommentLongClickListener(null)
    }
}