package ru.maxim.barybians.ui.fragment.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.FragmentChatBinding
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.chat.ChatRecyclerAdapter.OutgoingMessageViewHolder
import ru.maxim.barybians.ui.fragment.chat.OutgoingMessage.MessageStatus.*
import ru.maxim.barybians.ui.fragment.stickerPicker.StickersPickerDialog
import ru.maxim.barybians.utils.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ChatFragment : MvpAppCompatFragment(), ChatView {

    @Inject
    lateinit var presenterProvider: Provider<ChatPresenter>

    private val chatPresenter by moxyPresenter { presenterProvider.get() }

    private lateinit var binding: FragmentChatBinding

    private val args by navArgs<ChatFragmentArgs>()

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val interlocutorId by lazy { args.userId }
    private val messageItems = ArrayList<MessageItem>()
    private val message = MutableLiveData<String?>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            messageText = message
            lifecycleOwner = viewLifecycleOwner

            chatMessageSendBtn.setOnClickListener {
                if (message.value.isNotNullOrBlank()) sendMessage(message.value!!)
            }

            chatMessageEmojiBtn.setOnClickListener {
                val stickersPicker = StickersPickerDialog.newInstance {
                    // TODO: Implement sticker sending
                    context?.toast(it)
                }
                stickersPicker.show(childFragmentManager, "StickersPicker")
            }

            chatToolbarUser.root.setOnClickListener {
                findNavController().navigate(ChatFragmentDirections.toProfile(interlocutorId))
            }
            chatBackBtn.setOnClickListener { findNavController().popBackStack() }
        }

        if (savedInstanceState.isNull()) {
            chatPresenter.loadMessages(interlocutorId)
        }
    }

    override fun showMessages(messages: ArrayList<Message>, interlocutor: User) {
        with(binding) {
            chatLoading.visibility = GONE
            chatToolbarUser.userName = interlocutor.fullName
            chatToolbarUser.userAvatar = interlocutor.avatarMin
        }
        val currentUserId = preferencesManager.userId
        messageItems.clear()
        messageItems.addAll(messages.map {
            val time = simpleDate(it.date)
            val viewHolderId = "${it.text}${it.date}".hashCode().toLong()
            val status = if (it.unread == 1) Unread else Read
            return@map if (it.senderId == currentUserId)
                OutgoingMessage(viewHolderId, it.text, time, status)
            else
                IncomingMessage(viewHolderId, it.text, time, it.senderId)
        })
        binding.chatRecyclerView.apply {
            adapter = ChatRecyclerAdapter(messageItems)
            scrollToPosition(messageItems.size - 1)
        }
    }

    private fun sendMessage(text: String) {
        val timestamp = Date().time
        val time = time(timestamp)
        val viewHolderId = "${text}${timestamp}".hashCode().toLong()
        messageItems.add(OutgoingMessage(viewHolderId, text, time, Sending))
        binding.chatRecyclerView.apply {
            adapter?.notifyItemInserted(messageItems.size - 1)
            smoothScrollToPosition(messageItems.size - 1)
        }
        message.postValue(null)
        chatPresenter.sendMessage(interlocutorId, text, viewHolderId)
    }

    override fun onLoadingMessagesError() {
        binding.chatLoading.visibility = GONE
        context?.toast(R.string.an_error_occurred_while_loading_messages)
    }

    override fun showNoInternet() {
        binding.chatLoading.visibility = GONE
        context?.toast(R.string.no_internet_connection)
    }

    override fun onMessageSent(text: String, messageId: Long) {
        (binding.chatRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)
            ?.setUnreadLabel()
    }

    override fun onMessageSendingError(messageId: Long) {
        (binding.chatRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)
            ?.setErrorLabel()
    }

    override fun onMessagesReceived(messages: ArrayList<Message>) {
        messages.forEach {
            when (it.senderId) {
                interlocutorId -> {
                    val time = time(it.date)
                    val viewHolderId = "${it.text}${it.date}".hashCode().toLong()
                    messageItems.add(IncomingMessage(viewHolderId, it.text, time, it.senderId))
                    binding.chatRecyclerView.adapter?.notifyItemInserted(messageItems.size)
                }
                preferencesManager.userId -> {
                    val time = time(it.date)
                    val viewHolderId = "${it.text}${it.date}".hashCode().toLong()
                    messageItems.add(OutgoingMessage(viewHolderId, it.text, time, Unread))
                    binding.chatRecyclerView.adapter?.notifyItemInserted(messageItems.size)
                }
            }
        }
        binding.chatRecyclerView.apply {
            val lastVisibleItemPosition =
                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            if (lastVisibleItemPosition >= messageItems.size - 2) {
                smoothScrollToPosition(messageItems.size - 1)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        chatPresenter.stopObserving()
    }
}