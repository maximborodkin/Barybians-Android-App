package ru.maxim.barybians.ui.fragment.profile

import ru.maxim.barybians.ui.fragment.feed.PostsListAdapterListener

interface ProfileItemsListener : PostsListAdapterListener {

    fun onBackButtonClick()
    fun onPreferencesButtonClick()
    fun onStatusClick()
    fun onOpenChatButtonClick(userId: Int)
}