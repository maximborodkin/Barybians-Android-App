package ru.maxim.barybians.ui.fragment.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import org.koin.android.ext.android.inject
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentChatBinding
import ru.maxim.barybians.model.Message
import ru.maxim.barybians.model.User
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.fragment.chat.ChatRecyclerAdapter.OutgoingMessageViewHolder
import ru.maxim.barybians.ui.fragment.chat.OutgoingMessage.MessageStatus.*
import ru.maxim.barybians.ui.fragment.stickerPicker.StickersPickerDialog
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.isNotNullOrBlank
import ru.maxim.barybians.utils.isNull
import ru.maxim.barybians.utils.toast
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : MvpAppCompatFragment(), ChatView {

    @InjectPresenter
    lateinit var chatPresenter: ChatPresenter
    private lateinit var binding: FragmentChatBinding
    private val args by navArgs<ChatFragmentArgs>()
    private val preferencesManager: PreferencesManager by inject()
    private val dateFormatUtils: DateFormatUtils by inject()

    private val interlocutorId by lazy { args.userId }
    private val messageItems = ArrayList<MessageItem>()
    private val message = MutableLiveData<String?>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
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

        if (savedInstanceState.isNull()) { chatPresenter.loadMessages(interlocutorId) }
    }

    override fun showMessages(messages: ArrayList<Message>, interlocutor: User) {
        with(binding) {
            chatLoading.visibility = GONE
            chatToolbarUser.userName = interlocutor.fullName
            chatToolbarUser.userAvatar = interlocutor.getAvatarUrl(loadFull = false)
        }
        val currentUserId = preferencesManager.userId
        messageItems.clear()
        messageItems.addAll(messages.map {
            val time = dateFormatUtils.getTime(it.time * 1000)
            val viewHolderId = "${it.text}${it.time}".hashCode().toLong()
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
        val time = dateFormatUtils.getTime(timestamp)
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
                    val time = dateFormatUtils.getTime(it.time)
                    val viewHolderId = "${it.text}${it.time}".hashCode().toLong()
                    messageItems.add(IncomingMessage(viewHolderId, it.text, time, it.senderId))
                    binding.chatRecyclerView.adapter?.notifyItemInserted(messageItems.size)
                }
                preferencesManager.userId -> {
                    val time = dateFormatUtils.getTime(it.time)
                    val viewHolderId = "${it.text}${it.time}".hashCode().toLong()
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