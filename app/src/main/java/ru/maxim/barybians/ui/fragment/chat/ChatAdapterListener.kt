package ru.maxim.barybians.ui.fragment.chat

interface ChatAdapterListener {
    fun onMessageCopy(messageId: Int)
    fun onMessageRetry(messageId: Int)
}