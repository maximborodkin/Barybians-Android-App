package ru.maxim.barybians.ui.activity.dialog

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_dialog.*
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Message
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.base.BaseActivity
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.isNull
import ru.maxim.barybians.utils.toast

class DialogActivity : BaseActivity(), DialogView {

    @InjectPresenter
    lateinit var dialogPresenter: DialogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        if (savedInstanceState.isNull()){
            val userId = intent.getIntExtra("userId", 0)
            dialogPresenter.loadMessages(userId)
        }
    }

    override fun showMessages(messages: ArrayList<Message>) {
        val currentUserId = PreferencesManager.userId
        val messageItems = ArrayList<MessageItem>().apply {
            addAll(messages.map {
                val time = DateFormatUtils.getTime(it.time*1000)
                return@map if (it.senderId == currentUserId) OutgoingMessage(it.text, time)
                else IncomingMessage(it.text, time, it.senderId)
            })
        }
        dialogRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DialogActivity)
            adapter = DialogRecyclerAdapter(messageItems)
        }
    }

    override fun onLoadingMessagesError() {
        toast(getString(R.string.an_error_occurred_while_loading_messages))
    }

    override fun showNoInternet() {
        toast(R.string.no_internet_connection)
    }
}