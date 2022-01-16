package ru.maxim.barybians.ui.fragment.chatsList

import android.graphics.Typeface.BOLD
import android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ItemChatBinding
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.ui.fragment.chatsList.ChatsListRecyclerAdapter.DialogViewHolder
import ru.maxim.barybians.ui.view.AvatarView
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.load
import java.util.Date

class ChatsListRecyclerAdapter(
    private val chats: List<Chat>,
    private val currentUserId: Int,
    private val dateFormatUtils: DateFormatUtils,
    private val onChatClick: (userId: Int) -> Unit
) : RecyclerView.Adapter<DialogViewHolder>() {

    inner class DialogViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val avatar: AvatarView = binding.itemChatUserAvatar
        private val name: TextView = binding.itemChatUserName
        private val message: AppCompatTextView = binding.itemChatMessage
        private val date: TextView = binding.itemChatDate

        fun bind(chat: Chat) = with(binding) {
            avatar.load(chat.secondUser.avatarMin)
            avatar.isOnline = chat.secondUser.lastVisit >= Date().time / 1000 - 5 * 60
            name.text = chat.secondUser.fullName
            date.text = dateFormatUtils.getSimplifiedDate(chat.lastMessage.time * 1000)

            val lastMessage = SpannableStringBuilder().apply {
                if (chat.lastMessage.senderId == currentUserId) {
                    append(itemView.context.getString(R.string.you))
                    setSpan(StyleSpan(BOLD), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    append(chat.secondUser.firstName)
                    setSpan(StyleSpan(BOLD), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            message.text = lastMessage.append(": ${chat.lastMessage.text}").toString()

            itemView.setOnClickListener { onChatClick(chat.secondUser.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemChatBinding.inflate(layoutInflater)
        return DialogViewHolder(binding)
    }

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }
}