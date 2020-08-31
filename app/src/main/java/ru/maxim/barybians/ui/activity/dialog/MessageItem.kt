package ru.maxim.barybians.ui.activity.dialog

import ru.maxim.barybians.ui.activity.dialog.MessageType.IncomingMessage
import ru.maxim.barybians.ui.activity.dialog.MessageType.OutgoingMessage

sealed class MessageItem(val text: String, val time: String) {
    abstract fun getType(): Int
}

class IncomingMessage(text: String, time: String, val userId: Int) : MessageItem(text, time) {
    override fun getType(): Int = IncomingMessage.viewType
}

class OutgoingMessage(text: String,
                      time: String,
                      val viewHolderId: Long,
                      var status: MessageStatus
) : MessageItem(text, time) {
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

