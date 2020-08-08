package ru.maxim.barybians.ui.fragment.dialogsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.barybians.R


class DialogsListFragment : MvpAppCompatFragment(), DialogsListView {

    @InjectPresenter
    lateinit var dialogsListPresenter: DialogsListPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dialogs_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {}

    override fun showDialogsList() {}
}