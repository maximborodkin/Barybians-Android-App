package ru.maxim.barybians.ui.fragment.chat

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_incoming_message.view.*
import kotlinx.android.synthetic.main.item_outgoing_message.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.fragment.chat.OutgoingMessage.MessageStatus.*

class ChatRecyclerAdapter(
    private val messages: ArrayList<MessageItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    class IncomingMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView = view.itemIncomingMessageText
        val timeView: TextView = view.itemIncomingMessageTime
    }

    class OutgoingMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView = view.itemOutgoingMessageText
        val timeView: TextView = view.itemOutgoingMessageTime
        private val messageLabel: AppCompatImageView = view.itemOutgoingMessageLabel

        init { clearLabel() }

        fun setSendingProcessLabel() {
            messageLabel.setBackgroundResource(R.drawable.ic_sending_process_animated)
            (messageLabel.background as Animatable).start()
        }

        fun setErrorLabel() { messageLabel.setBackgroundResource(R.drawable.ic_error) }

        fun setUnreadLabel() { messageLabel.setBackgroundResource(R.drawable.unread_circle) }

        fun clearLabel() { messageLabel.background = null }
    }

    override fun getItemId(position: Int) = messages[position].viewId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MessageType.IncomingMessage.viewType -> IncomingMessageViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_incoming_message, parent, false)
            )
            MessageType.OutgoingMessage.viewType -> OutgoingMessageViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_outgoing_message, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when(getItemViewType(position)) {
            MessageType.IncomingMessage.viewType -> {
                val incomingMessage = message as? IncomingMessage
                (holder as? IncomingMessageViewHolder)?.let {
                    it.textView.text = incomingMessage?.text
                    it.timeView.text = incomingMessage?.time
                }
            }
            MessageType.OutgoingMessage.viewType -> {
                val outgoingMessage = message as? OutgoingMessage ?: return
                (holder as? OutgoingMessageViewHolder)?.let {
                    it.textView.text = outgoingMessage.text
                    it.timeView.text = outgoingMessage.time
                    when(outgoingMessage.status) {
                        Sending -> holder.setSendingProcessLabel()
                        Unread -> holder.setUnreadLabel()
                        Read -> holder.clearLabel()
                        Error -> holder.setErrorLabel()
                    }
                }
            }
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun getItemViewType(position: Int): Int = messages[position].getType()

    override fun getItemCount(): Int = messages.size

}