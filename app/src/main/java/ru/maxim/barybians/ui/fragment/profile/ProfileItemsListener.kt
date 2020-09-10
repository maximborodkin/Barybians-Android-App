package ru.maxim.barybians.ui.fragment.profile

import ru.maxim.barybians.ui.fragment.feed.FeedItemsListener

interface ProfileItemsListener : FeedItemsListener {
    fun popBackStack()
    fun openPreferences()
    fun showEditStatusDialog(status: String?)
    fun openDialog(userId: Int, userAvatar: String?, userName: String)
    fun editUserInfo() {}
    fun addPost(title: String?, text: String)
}