package ru.maxim.barybians.ui.fragment.profile

import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.base.BaseWallView

interface ProfileView : BaseWallView {

    fun showUserProfile(user: User)
    fun onUserLoadError()
    fun onStatusEdited(newStatus: String?)
    fun onPostCreated(post: Post)
    fun onPostCreateError()

}