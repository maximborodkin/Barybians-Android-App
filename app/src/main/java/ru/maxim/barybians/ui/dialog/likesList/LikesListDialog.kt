package ru.maxim.barybians.ui.dialog.likesList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentLikesListBinding
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class LikesListDialog : BottomSheetDialogFragment() {

    private val args: LikesListDialogArgs by navArgs()
    private var binding: FragmentLikesListBinding by notNull()

    @Inject
    lateinit var dateFormatUtils: DateFormatUtils

    @Inject
    lateinit var recyclerAdapter: LikesListRecyclerAdapter

    @Inject
    lateinit var factory: LikesListDialogViewModel.LikesListDialogViewModelFactory.Factory
    private val model: LikesListDialogViewModel by viewModels { factory.create(args.postId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikesListBinding.inflate(inflater, container, false)
        binding.apply {
            binding.likesListRecycler.adapter = recyclerAdapter
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.likes.collect { likes ->
                    recyclerAdapter.submitList(likes)

                    binding.likesListMessage.text =
                        if (likes.isEmpty()) view.context.getString(R.string.no_likes_yet)
                        else null

                    binding.likesListTitle.text = context?.resources?.getQuantityString(
                        R.plurals.like_plurals,
                        likes.size,
                        likes.size
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessageId.observe(viewLifecycleOwner) { errorMessageId ->
                errorMessageId?.let {
                    if (recyclerAdapter.itemCount == 0) {
                        binding.likesListMessage.text = view.context.getText(errorMessageId)
                    } else {
                        recyclerAdapter.notifyDataSetChanged()
                        context?.toast(it)
                    }
                }
            }
        }

        recyclerAdapter.setOnUserClickListener { userId ->

        }
    }
}