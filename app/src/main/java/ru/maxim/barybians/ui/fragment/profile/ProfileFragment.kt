package ru.maxim.barybians.ui.fragment.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentProfileBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.dialog.editText.EditTextDialog
import ru.maxim.barybians.ui.fragment.feed.PostsListFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileViewModel.ProfileViewModelFactory
import ru.maxim.barybians.utils.appComponent
import javax.inject.Inject

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val args: ProfileFragmentArgs by navArgs()

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory.Factory
    val model: ProfileViewModel by viewModels { profileViewModelFactory.create(args.userId) }

    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

//    @Inject
//    lateinit var headerRecyclerAdapter: ProfileHeaderRecyclerAdapter

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.user.collect(::bindProfileHeader)
            }
        }

        val feedFragment = PostsListFragment().apply {
            arguments = bundleOf(PostsListFragment.userIdKey to model.userId)
        }
        childFragmentManager
            .beginTransaction()
            .replace(binding.profilePostsListContainer.id, feedFragment)
            .commit()

        binding.profileRefreshLayout.setOnRefreshListener(feedFragment::refresh)
    }

    private fun bindProfileHeader(user: User?) = with(binding) {
        profileRefreshLayout.isRefreshing = false
        if (user == null) {

        } else {
            profileHeader.user = user
        }
    }

    fun onStatusClick() {
        EditTextDialog(
            context = context ?: return,
            title = getString(R.string.edit_status),
            text = model.user.value?.status,
            onPositiveButtonClicked = model::editStatus,
            maxCharactersCount = 50,
            hint = getString(R.string.status),
            isTextRequired = false
        ).show()
    }

    fun onAvatarClick(imageUrl: String) =
        findNavController().navigate(ProfileFragmentDirections.toImageViewer(imageUrl))

    fun onOpenChatButtonClick(userId: Int) =
        findNavController().navigate(ProfileFragmentDirections.toChat(userId))

//    override fun onDestroyView() {
//        headerRecyclerAdapter.setProfileItemsListener(null)
//        super.onDestroyView()
//    }
}