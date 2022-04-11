package ru.maxim.barybians.ui.fragment.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.FragmentChatBinding
import ru.maxim.barybians.ui.dialog.stickerPicker.StickersPickerDialog
import ru.maxim.barybians.ui.fragment.chat.ChatViewModel.ChatViewModelFactory
import ru.maxim.barybians.utils.appComponent
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class ChatFragment : Fragment() {

    private val args by navArgs<ChatFragmentArgs>()

    @Inject
    lateinit var factory: ChatViewModelFactory.Factory
    private val model: ChatViewModel by viewModels { factory.create(args.userId) }

    @Inject
    lateinit var chatRecyclerAdapter: ChatRecyclerAdapter

    private var binding: FragmentChatBinding by notNull()

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChatBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
            chatRecyclerView.adapter = chatRecyclerAdapter.setAdapterListener(null)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: collect paging data loading state

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                model.messages.collect(chatRecyclerAdapter::submitData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                model.interlocutor.collect { user ->
                    chatToolbarInterlocutor.user = user
                }
            }
        }
        // TODO: apply different drawables for sendButton when model.isLoading true and false
        chatMessageSendBtn.setOnClickListener { model.sendMessage() }

        chatMessageEmojiBtn.setOnClickListener {
            StickersPickerDialog().show(childFragmentManager, StickersPickerDialog::class.qualifiedName)
        }
        childFragmentManager.setFragmentResultListener(
            StickersPickerDialog.stickerPickerResultKey,
            viewLifecycleOwner
        ) { _, result: Bundle ->
            model.sendSticker(
                pack = result.getString(StickersPickerDialog.stickerPackKey),
                sticker = result.getString(StickersPickerDialog.stickerKey)
            )
        }

        chatToolbarInterlocutor.root.setOnClickListener {
            findNavController().navigate(ChatFragmentDirections.toProfile(args.userId))
        }
        chatToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

//    override fun showMessages(messages: List<Message>, interlocutor: User) {
//        with(binding) {
//            chatLoading.visibility = GONE
//            chatToolbarUser.userName = interlocutor.fullName
//            chatToolbarUser.userAvatar = interlocutor.avatarMin
//        }
//        val currentUserId = preferencesManager.userId
//        messageItems.clear()
//        messageItems.addAll(messages.map {
//            val time = simpleDate(it.date)
//            val viewHolderId = "${it.text}${it.date}".hashCode().toLong()
//            val status = if (it.isUnread) Unread else Read
//            return@map if (it.senderId == currentUserId)
//                OutgoingMessage(viewHolderId, it.text, time, status)
//            else
//                IncomingMessage(viewHolderId, it.text, time, it.senderId)
//        })
//        binding.chatRecyclerView.apply {
//            adapter = ChatRecyclerAdapter(messageItems)
//            scrollToPosition(messageItems.size - 1)
//        }
//    }
//
//    private fun sendMessage(text: String) {
//        val time = time(Date())
//        val viewHolderId = text.hashCode().toLong()
//        messageItems.add(OutgoingMessage(viewHolderId, text, time, Sending))
//        binding.chatRecyclerView.apply {
//            adapter?.notifyItemInserted(messageItems.size - 1)
//            smoothScrollToPosition(messageItems.size - 1)
//        }
//        message.postValue(null)
//        chatPresenter.sendMessage(interlocutorId, text, viewHolderId)
//    }
//
//    override fun onLoadingMessagesError() {
//        binding.chatLoading.visibility = GONE
//        context?.toast(R.string.an_error_occurred_while_loading_messages)
//    }
//
//    override fun showNoInternet() {
//        binding.chatLoading.visibility = GONE
//        context?.toast(R.string.no_internet_connection)
//    }
//
//    override fun onMessageSent(text: String, messageId: Long) {
//        (binding.chatRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)
//            ?.setUnreadLabel()
//    }
//
//    override fun onMessageSendingError(messageId: Long) {
//        (binding.chatRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)?.setErrorLabel()
//    }
//
//    override fun onMessagesReceived(messages: List<Message>) {
//        messages.forEach {
//            when (it.senderId) {
//                interlocutorId -> {
//                    val time = time(it.date)
//                    val viewHolderId = "${it.text}${it.date}".hashCode().toLong()
//                    messageItems.add(IncomingMessage(viewHolderId, it.text, time, it.senderId))
//                    binding.chatRecyclerView.adapter?.notifyItemInserted(messageItems.size)
//                }
//                preferencesManager.userId -> {
//                    val time = time(it.date)
//                    val viewHolderId = "${it.text}${it.date}".hashCode().toLong()
//                    messageItems.add(OutgoingMessage(viewHolderId, it.text, time, Unread))
//                    binding.chatRecyclerView.adapter?.notifyItemInserted(messageItems.size)
//                }
//            }
//        }
//        binding.chatRecyclerView.apply {
//            val lastVisibleItemPosition =
//                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
//            if (lastVisibleItemPosition >= messageItems.size - 2) {
//                smoothScrollToPosition(messageItems.size - 1)
//            }
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        chatPresenter.stopObserving()
//    }
}