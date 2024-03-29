package ru.maxim.barybians.ui.fragment.chatsList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.FragmentChatsListBinding
import ru.maxim.barybians.ui.fragment.chatsList.ChatsListViewModel.ChatsListViewModelFactory
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

class ChatsListFragment : Fragment(R.layout.fragment_chats_list) {

    @Inject
    lateinit var factory: ChatsListViewModelFactory
    private val model: ChatsListViewModel by viewModels { factory }

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var chatsListAdapter: ChatsListRecyclerAdapter

    private val binding by viewBinding(FragmentChatsListBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        chatsListAdapter.setOnChatClickListener { userId ->
            findNavController().navigate(ChatsListFragmentDirections.toChat(userId))
        }
        chatsListRecyclerView.adapter = chatsListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                model.chats.collect { chats ->
                    chatsListAdapter.submitList(chats)
                    chatsEmptyListMessage.isVisible = chats.isEmpty()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.isLoading.observe(viewLifecycleOwner) { isLoading ->
                chatsListLoading.isVisible = isLoading
                if (!isLoading) chatsListRefreshLayout.isRefreshing = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessage.observe(viewLifecycleOwner) { errorMessageId ->
                if (chatsListAdapter.itemCount > 0) {
                    context.toast(errorMessageId)
                } else {
                    chatsEmptyListMessage.text = errorMessageId?.let { getString(it) }
                }
            }
        }

        chatsListAddNewBtn.setOnClickListener { context.toast("New chat") }
        chatsListRefreshLayout.setOnRefreshListener { model.refresh() }
        model.refresh()
    }

    override fun onDestroyView() {
        chatsListAdapter.setOnChatClickListener(null)
        super.onDestroyView()
    }
}

//    override fun showChatsList(chatsList: List<Chat>) {
//        with(binding) {
//            chatsListLoading.isVisible = false
//            chatsListRefreshLayout.isRefreshing = false
//            chatsEmptyListLabel.isVisible = chatsList.isEmpty()
//            chatsListRecyclerView.apply {
//                adapter = ChatsListRecyclerAdapter(
//                    chats = chatsList,
//                    currentUserId = preferencesManager.userId,
//                    onChatClick = { userId ->
//                        findNavController().navigate(ChatsListFragmentDirections.toChat(userId))
//                    }
//                )
//            }
//        }
//    }
//
//    override fun showLoading() = with(binding) {
//        chatsEmptyListLabel.isVisible = false
//        if (!chatsListRefreshLayout.isRefreshing) {
//            chatsListLoading.isVisible = true
//        }
//    }
//
//    override fun showChatsListLoadError() = with(binding) {
//        context.toast(R.string.an_error_occurred_while_loading_chats)
//        chatsListLoading.isVisible = false
//        chatsListRefreshLayout.isRefreshing = false
//        chatsEmptyListLabel.isVisible = false
//    }
//
//    override fun showNoInternet() = with(binding) {
//        context.toast(R.string.no_internet_connection)
//        chatsListLoading.isVisible = false
//        chatsListRefreshLayout.isRefreshing = false
//        chatsEmptyListLabel.isVisible = false
//    }