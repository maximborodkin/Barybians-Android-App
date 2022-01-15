package ru.maxim.barybians.ui.fragment.profile

import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.base.BaseWallView

interface ProfileView : BaseWallView {

    @AddToEnd
    fun showUserProfile(user: User)

    @OneExecution
    fun onUserLoadError()

    @AddToEnd
    fun onStatusEdited(newStatus: String?)

    @AddToEnd
    fun onPostCreated(post: Post)

    @OneExecution
    fun onPostCreateError()
}