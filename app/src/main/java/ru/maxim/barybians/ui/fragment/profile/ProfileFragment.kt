package ru.maxim.barybians.ui.fragment.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.dialog.editText.EditTextDialog
import ru.maxim.barybians.ui.fragment.feed.FeedFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileViewModel.ProfileViewModelFactory
import ru.maxim.barybians.utils.appComponent
import javax.inject.Inject

class ProfileFragment : FeedFragment(), ProfileItemsListener {

    private val args: ProfileFragmentArgs by navArgs()

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory.Factory
    override val model: ProfileViewModel by viewModels { profileViewModelFactory.create(args.userId) }

    @Inject
    lateinit var headerRecyclerAdapter: ProfileHeaderRecyclerAdapter

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.user.collect { user ->
                    headerRecyclerAdapter.submitList(listOf(user))
                }
            }
        }
    }

    override fun refresh() {
        super.refresh()
        model.refreshUser()
    }

    override fun setupRecyclerView() {
        loadingStateAdapter.setOnRetryListener(feedRecyclerAdapter::retry)
        feedRecyclerAdapter.setAdapterListener(this)
        headerRecyclerAdapter.setProfileItemsListener(this)
        headerRecyclerAdapter.submitList(listOf(null))
        val profileRecyclerAdapter = ConcatAdapter(
            headerRecyclerAdapter,
            feedRecyclerAdapter.withLoadStateFooter(loadingStateAdapter)
        )
        binding.feedRecyclerView.adapter = profileRecyclerAdapter
    }

    override fun onBackButtonClick() {
        findNavController().popBackStack()
    }

    override fun onPreferencesButtonClick() {
        findNavController().navigate(ProfileFragmentDirections.profileToSettings())
    }

    override fun onStatusClick() {
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

    override fun onOpenChatButtonClick(userId: Int) {
        findNavController().navigate(ProfileFragmentDirections.toChat(userId))
    }

    override fun onDestroyView() {
        headerRecyclerAdapter.setProfileItemsListener(null)
        super.onDestroyView()
    }
}