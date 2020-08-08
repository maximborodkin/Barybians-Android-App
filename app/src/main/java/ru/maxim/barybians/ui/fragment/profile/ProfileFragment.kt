package ru.maxim.barybians.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.barybians.R

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun showNoInternet() {}

    override fun showUserProfile() {}

    override fun onUserLoadError() {}

    override fun onStatusEdited() {}

    override fun onPostCreated() {}

    override fun onPostCreateError() {}

    override fun onPostUpdated() {}

    override fun onPostUpdateError() {}

    override fun onPostDeleted() {}

    override fun onPostDeleteError() {}

    override fun onCommentAdded() {}

    override fun onCommentAddError() {}

    override fun onCommentRemoved() {}

    override fun onCommentRemoveError() {}

    override fun onLikeEdited() {}

}