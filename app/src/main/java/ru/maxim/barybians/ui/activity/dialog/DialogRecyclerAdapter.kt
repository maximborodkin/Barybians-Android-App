package ru.maxim.barybians.ui.activity.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.incoming_message.view.*
import kotlinx.android.synthetic.main.outgoing_message.view.*
import ru.maxim.barybians.R

class DialogRecyclerAdapter(
    private val messages: ArrayList<MessageItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class IncomingMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView = view.itemIncomingMessageText
        val timeView: TextView = view.itemIncomingMessageTime
    }

    class OutgoingMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView = view.itemOutgoingMessageText
        val timeView: TextView = view.itemOutgoingMessageTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MessageType.IncomingMessage.viewType -> IncomingMessageViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.incoming_message, parent, false)
            )
            MessageType.OutgoingMessage.viewType -> OutgoingMessageViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.outgoing_message, parent, false)
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
                val outgoingMessage = message as? OutgoingMessage
                (holder as? OutgoingMessageViewHolder)?.let {
                    it.textView.text = outgoingMessage?.text
                    it.timeView.text = outgoingMessage?.time
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = messages[position].getType()

    override fun getItemCount(): Int = messages.size

}