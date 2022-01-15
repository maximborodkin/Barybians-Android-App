package ru.maxim.barybians.ui.fragment.dialogsList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_dialogs_list.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import javax.inject.Provider

class DialogsListFragment : MvpAppCompatFragment(), DialogsListView {

    @Inject
    lateinit var presenterProvider: Provider<DialogsListPresenter>

    private val dialogsListPresenter by moxyPresenter { presenterProvider.get() }

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var dateFormatUtils: DateFormatUtils

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_dialogs_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogsListRefreshLayout.setOnRefreshListener { dialogsListPresenter.loadDialogsList() }
    }

    override fun showDialogsList(dialogsList: ArrayList<Chat>) {
        dialogsListLoading.visibility = GONE
        dialogsListRefreshLayout.isRefreshing = false
        dialogsListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DialogsListRecyclerAdapter(
                dialogsList,
                preferencesManager.userId,
                dateFormatUtils
            ) { userId ->
                findNavController().navigate(DialogsListFragmentDirections.toChat(userId))
            }
        }
    }

    override fun showLoading() {
        if (!dialogsListRefreshLayout.isRefreshing)
            dialogsListLoading.visibility = VISIBLE
    }

    override fun onDialogsListLoadError() {
        dialogsListLoading.visibility = GONE
        dialogsListRefreshLayout.isRefreshing = false
        context?.toast(R.string.an_error_occurred_while_loading_dialogs)
    }

    override fun showNoInternet() {
        dialogsListRefreshLayout.isRefreshing = false
        dialogsListLoading.visibility = GONE
        context?.toast(R.string.no_internet_connection)
    }
}