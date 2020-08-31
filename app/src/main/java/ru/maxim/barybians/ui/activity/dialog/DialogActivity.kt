package ru.maxim.barybians.ui.activity.dialog

import android.os.Bundle
import android.view.View.GONE
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_dialog.*
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Message
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.base.BaseActivity
import ru.maxim.barybians.ui.activity.dialog.DialogRecyclerAdapter.OutgoingMessageViewHolder
import ru.maxim.barybians.ui.activity.dialog.OutgoingMessage.MessageStatus.*
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.isNull
import ru.maxim.barybians.utils.toast
import java.util.*
import kotlin.collections.ArrayList

class DialogActivity : BaseActivity(), DialogView {

    @InjectPresenter
    lateinit var dialogPresenter: DialogPresenter
    private var interlocutorId = 0
    private val messageItems = ArrayList<MessageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        if (savedInstanceState.isNull()){
            interlocutorId = intent.getIntExtra("userId", 0)
            dialogPresenter.loadMessages(interlocutorId)
        }
        dialogMessageInput.addTextChangedListener {
            val tintColorId = if (it.isNullOrBlank()) R.color.send_btn_disabled_color
            else R.color.send_btn_enabled_color
            (dialogMessageSendBtn.setColorFilter(ContextCompat.getColor(this, tintColorId)))
        }
        dialogMessageSendBtn.setOnClickListener {
            val messageText = dialogMessageInput.text.toString()
            if (messageText.isNotBlank()) sendMessage(messageText)
        }
    }

    override fun showMessages(messages: ArrayList<Message>) {
        dialogLoading.visibility = GONE
        val currentUserId = PreferencesManager.userId
        messageItems.clear()
        messageItems.addAll(messages.map {
            val time = DateFormatUtils.getTime(it.time*1000)
            val viewHolderId = "${it.text}${it.time}".hashCode().toLong()
            val status = if (it.unread == 1) Unread else Read
            return@map if (it.senderId == currentUserId)
                OutgoingMessage(it.text, time, viewHolderId, status)
            else
                IncomingMessage(it.text, time, it.senderId)
        })
        dialogRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DialogActivity)
            adapter = DialogRecyclerAdapter(messageItems)
            scrollToPosition(messageItems.size-1)
        }
    }

    private fun sendMessage(text: String) {
        val timestamp = Date().time
        val time = DateFormatUtils.getTime(timestamp)
        val viewHolderId = "${text}${timestamp}".hashCode().toLong()
        messageItems.add(OutgoingMessage(text, time, viewHolderId, Sending))
        dialogRecyclerView.apply {
            adapter?.notifyItemInserted(messageItems.size - 1)
            smoothScrollToPosition(messageItems.size - 1)
        }
        dialogMessageInput.text = null
        dialogPresenter.sendMessage(interlocutorId, text, viewHolderId)
    }

    override fun onLoadingMessagesError() {
        dialogLoading.visibility = GONE
        toast(R.string.an_error_occurred_while_loading_messages)
    }

    override fun showNoInternet() {
        dialogLoading.visibility = GONE
        toast(R.string.no_internet_connection)
    }

    override fun onMessageSent(text: String, messageId: Long) {
        (dialogRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)
            ?.setUnreadLabel()
    }

    override fun onMessageSendingError(messageId: Long) {
        (dialogRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)
            ?.setErrorLabel()
    }

    override fun onMessageReceived(message: Message) {
        TODO("Not yet implemented")
    }
}