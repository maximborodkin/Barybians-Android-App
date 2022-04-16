package ru.maxim.barybians.ui.fragment.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemIncomingMessageBinding
import ru.maxim.barybians.databinding.ItemOutgoingMessageBinding
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.utils.adaptiveDate
import javax.inject.Inject

class ChatRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager
) : PagingDataAdapter<Message, RecyclerView.ViewHolder>(MessageDiffUtils) {

    private var chatAdapterListener: ChatAdapterListener? = null

    fun setAdapterListener(listener: ChatAdapterListener?): ChatRecyclerAdapter {
        chatAdapterListener = listener
        return this
    }

    class IncomingMessageViewHolder(private val binding: ItemIncomingMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) = with(binding) {
            // TODO: apply attachments to message
            incomingMessageText.text = message.text
            incomingMessageTime.text = adaptiveDate(message.date, hasTime = true)
        }
    }

    class OutgoingMessageViewHolder(private val binding: ItemOutgoingMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) = with(binding) {
            // TODO: apply attachments to message
            outgoingMessageText.text = message.text
            outgoingMessageTime.text = adaptiveDate(message.date, hasTime = true)
//            when (message.status) {
//                Sending -> {
//                    outgoingMessageStatus.setImageResource(R.drawable.ic_timer_animated)
//                    (outgoingMessageStatus.background as? Animatable)?.start()
//                }
//                Unread -> outgoingMessageStatus.setImageResource(R.drawable.unread_circle)
//                Read -> outgoingMessageStatus.setImageDrawable(null)
//                Error -> outgoingMessageStatus.setImageResource(R.drawable.ic_error)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageType.INCOMING.viewType -> IncomingMessageViewHolder(
                ItemIncomingMessageBinding.inflate(layoutInflater, parent, false)
            )
            MessageType.OUTGOING.viewType -> OutgoingMessageViewHolder(
                ItemOutgoingMessageBinding.inflate(layoutInflater, parent, false)
            )
            else -> throw IllegalArgumentException("No ViewHolder for viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position)?.senderId == preferencesManager.userId) MessageType.OUTGOING.viewType
        else MessageType.INCOMING.viewType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position) ?: return
        when (getItemViewType(position)) {
            MessageType.INCOMING.viewType -> {
                (holder as? IncomingMessageViewHolder)?.bind(message)
            }
            MessageType.OUTGOING.viewType -> {
                (holder as? OutgoingMessageViewHolder)?.bind(message)
            }
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    private object MessageDiffUtils : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem.messageId == newItem.messageId

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem == newItem
    }

    private enum class MessageType(val viewType: Int) {
        INCOMING(1),
        OUTGOING(2)
    }
}