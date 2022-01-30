package ru.maxim.barybians.ui.fragment.profile

import ru.maxim.barybians.ui.fragment.feed.FeedItemsListener

interface ProfileItemsListener : FeedItemsListener {

    fun onBackButtonClick()
    fun onPreferencesButtonClick()
    fun onStatusClick()
    fun onOpenChatButtonClick(userId: Int)
    fun onCreatePost(title: String?, text: String)
}