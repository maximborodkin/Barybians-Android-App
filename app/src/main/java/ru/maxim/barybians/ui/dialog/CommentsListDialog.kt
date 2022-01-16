package ru.maxim.barybians.ui.dialog

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.FragmentCommentsListBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.fragment.feed.CommentsRecyclerAdapter
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.HtmlParser
import ru.maxim.barybians.utils.appComponent
import javax.inject.Inject

class CommentsListDialog : BottomSheetDialogFragment(), Refreshable {

    @Inject
    lateinit var preferencesManager: PreferencesManager

//    @Inject
//    lateinit var htmlParser: HtmlParser

    @Inject
    lateinit var dateFormatUtils: DateFormatUtils


    private var binding: FragmentCommentsListBinding? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val commentsCount = comments.size

        binding?.commentsListTitle?.text = if (commentsCount == 0) {
            context?.getString(R.string.no_comments_yet)
        } else {
            context?.resources?.getQuantityString(
                R.plurals.comment_plurals,
                commentsCount,
                commentsCount
            )
        }

        binding?.commentsListRecycler?.adapter = CommentsRecyclerAdapter(
            currentUserId = preferencesManager.userId,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onCommentSwipe = onCommentDelete,
//            htmlParser = htmlParser,
            dateFormatUtils = dateFormatUtils
        )

        binding?.commentsListTextEditor?.addTextChangedListener {
            val buttonTintResource =
                if (it.isNullOrBlank()) R.color.send_btn_disabled_color
                else R.color.send_btn_enabled_color
            binding?.commentsListSendBtn?.setColorFilter(buttonTintResource)
        }
        binding?.commentsListSendBtn?.setOnClickListener {
            val commentText = binding?.commentsListTextEditor?.text.toString()
            if (commentText.isNotBlank()) {
                onCommentAdd(commentText)
            }
        }
    }

    override fun refresh() {
        binding?.commentsListRecycler
    }

    companion object {
        private lateinit var comments: List<Comment>
        private lateinit var onUserClick: (userId: Int) -> Unit
        private lateinit var onImageClick: (drawable: Drawable) -> Unit
        private lateinit var onCommentAdd: (text: String) -> Unit
        private lateinit var onCommentEdit: (commentId: Int, text: String) -> Unit
        private lateinit var onCommentDelete: (commentId: Int) -> Unit

        fun newInstance(
            comments: List<Comment>,
            onUserClick: (userId: Int) -> Unit,
            onImageClick: (drawable: Drawable) -> Unit,
            onCommentAdd: (text: String) -> Unit,
            onCommentEdit: (commentId: Int, text: String) -> Unit,
            onCommentDelete: (commentId: Int) -> Unit
        ): CommentsListDialog {
            this.comments = comments
            this.onUserClick = onUserClick
            this.onImageClick = onImageClick
            this.onCommentAdd = onCommentAdd
            this.onCommentEdit = onCommentEdit
            this.onCommentDelete = onCommentDelete
            return CommentsListDialog()
        }
    }
}