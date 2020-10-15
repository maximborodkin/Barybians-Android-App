package ru.maxim.barybians.ui.activity.dialog

import ru.maxim.barybians.ui.activity.dialog.MessageType.IncomingMessage
import ru.maxim.barybians.ui.activity.dialog.MessageType.OutgoingMessage

sealed class MessageItem(val viewId: Long, val text: String, val time: String) {
    abstract fun getType(): Int
}

class IncomingMessage(viewId: Long,
                      text: String,
                      time: String,
                      val senderId: Int
) : MessageItem(viewId, text, time) {
    override fun getType(): Int = IncomingMessage.viewType
}

class OutgoingMessage(viewId: Long,
                      text: String,
                      time: String,
                      var status: MessageStatus
) : MessageItem(viewId, text, time) {
    override fun getType(): Int = OutgoingMessage.viewType

    enum class MessageStatus {
        Sending,
        Unread,
        Read,
        Error
    }
}

enum class MessageType(val viewType: Int) {
    IncomingMessage(1),
    OutgoingMessage(2)
}

