package ru.maxim.barybians.ui.fragment.profile

import android.content.Context
import android.graphics.drawable.ColorDrawable
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
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.FragmentProfileBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.dialog.editText.EditTextDialog
import ru.maxim.barybians.ui.fragment.postsList.PostsListFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileViewModel.ProfileViewModelFactory
import ru.maxim.barybians.utils.*
import javax.inject.Inject

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val args: ProfileFragmentArgs by navArgs()

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var htmlUtils: HtmlUtils

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory.Factory
    val model: ProfileViewModel by viewModels { profileViewModelFactory.create(args.userId) }

    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

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
        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.profileRefreshLayout.isRefreshing = false
                context.toast(errorMessage)
            }
        }

        val postsListFragment = PostsListFragment().apply {
            arguments = bundleOf(PostsListFragment.userIdKey to model.userId)
        }
        childFragmentManager
            .beginTransaction()
            .replace(binding.profilePostsListContainer.id, postsListFragment)
            .commit()

        binding.profileRefreshLayout.apply {
            setOnRefreshListener {
                model.refreshUser()
                postsListFragment.refresh()
            }

            var isExpanded = true
            binding.profileAppBarLayout.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                    isExpanded = verticalOffset == 0
                }
            )
            setOnChildScrollUpCallback { _, _ -> !isExpanded }
        }
    }

    private fun bindProfileHeader(user: User?) = with(binding) {
        profileRefreshLayout.isRefreshing = false
        if (user == null) {
            binding.isPersonal = true
            profilePreferencesButton.hide()
            profileBackgroundPicture.setImageDrawable(null)
            profileAvatar.setImageDrawable(ColorDrawable(profileCardBackground.solidColor))
            profileName.text = null
            profileOnlineStatus.text = null
            profileAge.hide()
            profileStatus.text = null
            profileOpenChatButton.hide()
        } else {
            profileAge.show()
            profilePreferencesButton.show()
            binding.isDebug = preferencesManager.isDebug
            binding.isPersonal = preferencesManager.userId == model.userId
            binding.user = user

            profileAvatar.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toImageViewer(user.avatarFull))
            }

            profilePreferencesButton.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.profileToPreferences())
            }

            profileOpenChatButton.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toChat(user.userId))
            }
            profileName.setOnClickListener { context.toast(user.role.stringResource) }
            profileStatus.text = htmlUtils.parseHtml(user.status ?: String()).first
            if (preferencesManager.userId == user.userId) {
                profileStatus.setOnClickListener {
                    EditTextDialog(
                        context = context ?: return@setOnClickListener,
                        title = getString(R.string.edit_status),
                        text = model.user.value?.status,
                        onPositiveButtonClicked = model::editStatus,
                        maxCharactersCount = 50,
                        hint = getString(R.string.status),
                        isTextRequired = false
                    ).show()
                }
            }
        }
    }
}