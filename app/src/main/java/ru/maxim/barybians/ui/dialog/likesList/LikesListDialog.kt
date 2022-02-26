package ru.maxim.barybians.ui.dialog.likesList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import ru.maxim.barybians.databinding.FragmentLikesListBinding
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class LikesListDialog : BottomSheetDialogFragment() {

    private val args: LikesListDialogArgs by navArgs()
    private var binding: FragmentLikesListBinding by notNull()

    @Inject
    lateinit var recyclerAdapter: LikesListRecyclerAdapter

    @Inject
    lateinit var factory: LikesListDialogViewModel.LikesListDialogViewModelFactory.Factory
    private val model: LikesListDialogViewModel by viewModels { factory.create(args.postId) }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikesListBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
            likesListRecycler.adapter = recyclerAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.likes.collect(recyclerAdapter::submitList)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessage.observe(viewLifecycleOwner) { messageResource -> context.toast(messageResource) }
        }

        recyclerAdapter.setOnUserClickListener { userId ->
            val action = LikesListDialogDirections.toProfile(userId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter.setOnUserClickListener(null)
    }
}