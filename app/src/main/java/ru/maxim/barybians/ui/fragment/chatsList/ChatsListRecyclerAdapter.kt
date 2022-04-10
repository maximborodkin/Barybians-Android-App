package ru.maxim.barybians.ui.fragment.chatsList

import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemChatBinding
import ru.maxim.barybians.domain.model.Attachment.AttachmentType.*
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.ui.fragment.chatsList.ChatsListRecyclerAdapter.ChatViewHolder
import javax.inject.Inject

class ChatsListRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
) : ListAdapter<Chat, ChatViewHolder>(ChatDiffUtils) {

    private var onChatClick: ((userId: Int) -> Unit)? = null

    fun setAdapterListener(listener: ((userId: Int) -> Unit)?) {
        onChatClick = listener
    }

    inner class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) = with(binding) {
            binding.chat = chat

            val messageText = when {
                chat.lastMessage.attachments.any { attachment -> attachment.type == STICKER } ->
                    itemView.context.getString(R.string.sticker)

                chat.lastMessage.attachments.any { attachment ->
                    attachment.type == IMAGE &&
                            attachment.offset == 0 &&
                            attachment.length == chat.lastMessage.text.length
                } ->
                    itemView.context.getString(R.string.image)

                chat.lastMessage.attachments.any { attachment ->
                    attachment.type == FILE &&
                            attachment.offset == 0 &&
                            attachment.length == chat.lastMessage.text.length
                } ->
                    itemView.context.getString(R.string.file)

                chat.lastMessage.attachments.any { attachment ->
                    attachment.type == LINK &&
                            attachment.offset == 0 &&
                            attachment.length == chat.lastMessage.text.length
                } ->
                    itemView.context.getString(R.string.link)

                else -> chat.lastMessage.text
            }

            val senderName =
                if (chat.lastMessage.senderId == preferencesManager.userId) itemView.context.getString(R.string.you)
                else chat.secondUser.firstName
            val lastMessage = SpannableString(
                itemView.context.getString(R.string.last_message_placeholder, senderName, messageText)
            )

            lastMessage.setSpan(StyleSpan(BOLD), 0, senderName.length, SPAN_EXCLUSIVE_EXCLUSIVE)
            itemChatMessage.setText(lastMessage, TextView.BufferType.SPANNABLE)

            itemView.setOnClickListener { onChatClick?.invoke(chat.secondUser.userId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemChatBinding.inflate(layoutInflater)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object ChatDiffUtils : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean =
            oldItem.secondUser.userId == newItem.secondUser.userId

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean =
            oldItem == newItem
    }
}