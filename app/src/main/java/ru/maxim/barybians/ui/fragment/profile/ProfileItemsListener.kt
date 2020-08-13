package ru.maxim.barybians.ui.fragment.profile

import ru.maxim.barybians.ui.fragment.feed.FeedItemsListener

interface ProfileItemsListener :
    FeedItemsListener {
    fun popBackStack()
    fun openPreferences() {}
    fun openDialog(userId: Int) {}
    fun editStatus(newStatus: String?) {}
    fun editUserInfo() {}
    fun addPost(title: String?, text: String)
}