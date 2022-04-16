package ru.maxim.barybians.ui.fragment.chat


abstract class MessageItem(
    val viewId: Long, val text: String, val time: String
    )

class IncomingMessage(
    viewId: Long,
    text: String,
    time: String,
    val senderId: Int
) : MessageItem(viewId, text, time)

class OutgoingMessage(
    viewId: Long,
    text: String,
    time: String,
    var status: MessageStatus
) : MessageItem(viewId, text, time) {


    enum class MessageStatus {
        Sending,
        Unread,
        Read,
        Error
    }
}



