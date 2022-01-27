package ru.maxim.barybians.ui.dialog.commentsList

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.repository.CommentRepository
import ru.maxim.barybians.databinding.FragmentCommentsListBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.dialog.Refreshable
import ru.maxim.barybians.ui.fragment.feed.CommentsRecyclerAdapter
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.autoCleared
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class CommentsListDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

//    @Inject
//    lateinit var htmlUtils: HtmlUtils

    @Inject
    lateinit var dateFormatUtils: DateFormatUtils

    private var binding: FragmentCommentsListBinding by notNull()

    private var recyclerAdapter: CommentsRecyclerAdapter by autoCleared()

    private val args: CommentsListDialogArgs by navArgs()

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
        binding = FragmentCommentsListBinding.inflate(inflater, container, false)
        recyclerAdapter = CommentsRecyclerAdapter(
            currentUserId = preferencesManager.userId,
            onUserClick = { userId ->
                dismiss()
//                onUserClick(userId)
            },
            onImageClick = { drawable ->
                dismiss()
//                onImageClick (drawable)
            },
            onCommentSwipe = model::deleteComment,
//            htmlParser = htmlParser,
            dateFormatUtils = dateFormatUtils
        )
        binding.commentsListRecycler.adapter = recyclerAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.comments.collect { comments ->
                    recyclerAdapter.submitList(comments)
                }
            }
        }


//        val commentsCount = comments.size

//        binding.commentsListTitle.text = if (commentsCount == 0) {
//            context?.getString(R.string.no_comments_yet)
//        } else {
//            context?.resources?.getQuantityString(
//                R.plurals.comment_plurals,
//                commentsCount,
//                commentsCount
//            )
//        }


        binding.commentsListTextEditor.addTextChangedListener {
            val buttonTintResource =
                if (it.isNullOrBlank()) R.color.send_btn_disabled_color
                else R.color.send_btn_enabled_color
            binding.commentsListSendBtn.setColorFilter(buttonTintResource)
        }
        binding.commentsListSendBtn.setOnClickListener {
            val commentText = binding.commentsListTextEditor.text.toString()
            if (commentText.isNotBlank()) {
                model.createComment(commentText)
            }
        }
    }
}