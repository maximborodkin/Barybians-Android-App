package ru.maxim.barybians.ui.fragment.profile

import com.arellomobile.mvp.MvpView

interface ProfileView : MvpView{

    fun showNoInternet()
    fun showUserProfile()
    fun onUserLoadError()
    fun onStatusEdited()
    fun onPostCreated()
    fun onPostCreateError()
    fun onPostUpdated()
    fun onPostUpdateError()
    fun onPostDeleted()
    fun onPostDeleteError()
    fun onCommentAdded()
    fun onCommentAddError()
    fun onCommentRemoved()
    fun onCommentRemoveError()
    fun onLikeEdited()
}