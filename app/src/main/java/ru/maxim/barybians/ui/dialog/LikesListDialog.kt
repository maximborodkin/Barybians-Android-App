package ru.maxim.barybians.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentLikesListBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.feed.LikesListRecyclerAdapter
import ru.maxim.barybians.utils.autoCleared

class LikesListDialog : BottomSheetDialogFragment(), Refreshable {

    private var binding: FragmentLikesListBinding? = null
    private var recyclerAdapter: LikesListRecyclerAdapter by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikesListBinding.inflate(inflater, container, false)
        recyclerAdapter = LikesListRecyclerAdapter(
            likes = likes,
            onUserClick = { userId ->
                dismiss()
                onUserClick(userId)
            }
        )
        binding?.likesListRecycler?.adapter = recyclerAdapter
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val likesCount = likes.size

        binding?.likesListTitle?.text = if (likesCount == 0) {
            context?.getString(R.string.nobody_like_this)
        } else {
            context?.resources?.getQuantityString(
                R.plurals.like_plurals,
                likesCount,
                likesCount
            )
        }
        refresh()
    }

    override fun refresh() {
        recyclerAdapter.submitList(likes)
    }

    companion object {
        private lateinit var likes: List<User>
        private lateinit var onUserClick: (userId: Int) -> Unit

        fun newInstance(
            likes: List<User>,
            onUserClick: (userId: Int) -> Unit
        ): LikesListDialog {
            this.likes = likes
            this.onUserClick = onUserClick
            return LikesListDialog()
        }
    }
}