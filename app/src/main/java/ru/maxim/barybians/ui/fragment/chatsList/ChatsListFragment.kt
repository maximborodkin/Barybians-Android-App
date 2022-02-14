package ru.maxim.barybians.ui.fragment.chatsList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.FragmentChatsListBinding
import ru.maxim.barybians.data.network.model.ChatDto
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import javax.inject.Provider


class ChatsListFragment : MvpAppCompatFragment(R.layout.fragment_chats_list), ChatsListView {

    @Inject
    lateinit var presenterProvider: Provider<ChatsListPresenter>
    private val dialogsListPresenter by moxyPresenter { presenterProvider.get() }

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val binding by viewBinding(FragmentChatsListBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            chatsListRefreshLayout.setOnRefreshListener(dialogsListPresenter::loadDialogsList)
            chatsListAddNewBtn.setOnClickListener {
                preferencesManager.isDarkMode = !preferencesManager.isDarkMode
                activity?.recreate()
                context?.toast("Create chat dialog #NotImplemented")
                // TODO: Add chat creation dialog
            }
        }
    }

    override fun showChatsList(chatsList: List<Chat>) {
        with(binding) {
            chatsListLoading.isVisible = false
            chatsListRefreshLayout.isRefreshing = false
            chatsEmptyListLabel.isVisible = chatsList.isEmpty()
            chatsListRecyclerView.apply {
                adapter = ChatsListRecyclerAdapter(
                    chats = chatsList,
                    currentUserId = preferencesManager.userId,
                    onChatClick = { userId ->
                        findNavController().navigate(ChatsListFragmentDirections.toChat(userId))
                    }
                )
            }
        }
    }

    override fun showLoading() = with(binding) {
        chatsEmptyListLabel.isVisible = false
        if (!chatsListRefreshLayout.isRefreshing) {
            chatsListLoading.isVisible = true
        }
    }

    override fun showChatsListLoadError() = with(binding) {
        context?.toast(R.string.an_error_occurred_while_loading_chats)
        chatsListLoading.isVisible = false
        chatsListRefreshLayout.isRefreshing = false
        chatsEmptyListLabel.isVisible = false
    }

    override fun showNoInternet() = with(binding) {
        context?.toast(R.string.no_internet_connection)
        chatsListLoading.isVisible = false
        chatsListRefreshLayout.isRefreshing = false
        chatsEmptyListLabel.isVisible = false
    }
}