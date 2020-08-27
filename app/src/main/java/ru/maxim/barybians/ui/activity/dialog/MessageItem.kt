package ru.maxim.barybians.ui.activity.dialog

import ru.maxim.barybians.ui.activity.dialog.MessageType.IncomingMessage
import ru.maxim.barybians.ui.activity.dialog.MessageType.OutgoingMessage

sealed class MessageItem {
    abstract fun getType(): Int
}

class IncomingMessage(val text: String, val time: String, val userId: Int) : MessageItem() {
    override fun getType(): Int = IncomingMessage.viewType
}

class OutgoingMessage(val text: String, val time: String) : MessageItem() {
    override fun getType(): Int = OutgoingMessage.viewType
}

enum class MessageType(val viewType: Int) {
    IncomingMessage(1),
    OutgoingMessage(2)
}