package ru.maxim.barybians.ui.fragment.chat

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ItemIncomingMessageBinding
import ru.maxim.barybians.databinding.ItemOutgoingMessageBinding
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.ui.fragment.chat.OutgoingMessage.MessageStatus.*
import javax.inject.Inject

class ChatRecyclerAdapter @Inject constructor(
) : PagingDataAdapter<Message, RecyclerView.ViewHolder>(MessageDiffUtils) {

//    init {
//        setHasStableIds(true)
//    }

    fun setAdapterListener(listener: (() -> Unit)?) : ChatRecyclerAdapter {

        return this
    }

    class IncomingMessageViewHolder(private val binding: ItemIncomingMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: IncomingMessage) = with(binding) {
            incomingMessageText.text = message.text
            incomingMessageTime.text = message.time
        }
    }

    class OutgoingMessageViewHolder(private val binding: ItemOutgoingMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: OutgoingMessage) = with(binding) {
            outgoingMessageStatus.setImageDrawable(null)
            outgoingMessageText.text = message.text
            outgoingMessageTime.text = message.time
            when (message.status) {
                Sending -> setSendingProcessLabel()
                Unread -> setUnreadLabel()
                Read -> clearLabel()
                Error -> setErrorLabel()
            }
        }

        private fun setSendingProcessLabel() = with(binding.outgoingMessageStatus) {
            setImageResource(R.drawable.ic_timer_animated)
            (background as? Animatable)?.start()
        }

        fun setErrorLabel() = with(binding.outgoingMessageStatus) {
            setImageResource(R.drawable.ic_error)
        }

        fun setUnreadLabel() = with(binding.outgoingMessageStatus) {
            setImageResource(R.drawable.unread_circle)
        }

        private fun clearLabel() = with(binding.outgoingMessageStatus) {
            setImageDrawable(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageType.IncomingMessage.viewType -> IncomingMessageViewHolder(
                ItemIncomingMessageBinding.inflate(layoutInflater, parent, false)
            )
            MessageType.OutgoingMessage.viewType -> OutgoingMessageViewHolder(
                ItemOutgoingMessageBinding.inflate(layoutInflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val message = messages[position]
//        when (getItemViewType(position)) {
//            MessageType.IncomingMessage.viewType -> {
//                (holder as? IncomingMessageViewHolder)?.let {
//                    (message as? IncomingMessage)?.let {
//                        holder.bind(message)
//                    }
//                }
//            }
//            MessageType.OutgoingMessage.viewType -> {
//                (holder as? OutgoingMessageViewHolder)?.let {
//                    (message as? OutgoingMessage)?.let {
//                        holder.bind(message)
//                    }
//                }
//            }
//            else -> throw IllegalStateException("Unknown view type")
//        }
    }

//    override fun getItemViewType(position: Int): Int = messages[position].getType()

//    override fun getItemCount(): Int = messages.size

    private object MessageDiffUtils : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem.messageId == newItem.messageId

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem == newItem
    }
}