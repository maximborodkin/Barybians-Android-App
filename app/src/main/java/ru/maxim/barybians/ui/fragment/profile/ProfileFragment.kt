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

        val feedFragment = PostsListFragment().apply {
            arguments = bundleOf(PostsListFragment.userIdKey to model.userId)
        }
        childFragmentManager
            .beginTransaction()
            .replace(binding.profilePostsListContainer.id, feedFragment)
            .commit()

        feedFragment.onRefresh = { model.refreshUser() }
    }

    private fun bindProfileHeader(user: User?) = with(binding) {
        if (user == null) {
            binding.isPersonal = true
            itemProfileHeaderPreferencesButton.hide()
            itemProfileHeaderImage.setImageDrawable(null)
            itemProfileHeaderAvatar.setImageDrawable(ColorDrawable(itemProfileHeaderCardBackground.solidColor))
            itemProfileHeaderAvatar.isOnline = false
            itemProfileHeaderName.text = null
            itemProfileHeaderAge.hide()
            itemProfileHeaderStatus.text = null
            itemProfileHeaderChatButton.hide()
        } else {
            binding.isDebug = preferencesManager.isDebug
            binding.isPersonal = preferencesManager.userId == model.userId
            binding.user = user

            itemProfileHeaderAge.show()

            itemProfileHeaderAvatar.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toImageViewer(user.avatarFull))
            }

            itemProfileHeaderPreferencesButton.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.profileToPreferences())
            }

            itemProfileHeaderChatButton.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toChat(user.userId))
            }
            itemProfileHeaderName.setOnClickListener { context?.toast(user.role.stringResource) }
            itemProfileHeaderStatus.text = htmlUtils.parseHtml(user.status ?: String()).first
            if (preferencesManager.userId == user.userId) {
                itemProfileHeaderStatus.setOnClickListener {
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