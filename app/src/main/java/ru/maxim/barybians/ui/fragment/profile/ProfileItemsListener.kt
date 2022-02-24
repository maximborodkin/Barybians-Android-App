package ru.maxim.barybians.ui.fragment.profile

import ru.maxim.barybians.ui.fragment.feed.FeedAdapterListener

interface ProfileItemsListener : FeedAdapterListener {

    fun onBackButtonClick()
    fun onPreferencesButtonClick()
    fun onStatusClick()
    fun onOpenChatButtonClick(userId: Int)
}