package ru.maxim.barybians.ui.activity.dialog

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ActivityDialogBinding
import ru.maxim.barybians.databinding.ToolbarUserBinding
import ru.maxim.barybians.model.Message
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.base.BaseActivity
import ru.maxim.barybians.ui.activity.dialog.DialogRecyclerAdapter.OutgoingMessageViewHolder
import ru.maxim.barybians.ui.activity.dialog.OutgoingMessage.MessageStatus.*
import ru.maxim.barybians.ui.activity.profile.ProfileActivity
import ru.maxim.barybians.ui.fragment.stickerPicker.StickersPickerDialog
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.isNotNullOrBlank
import ru.maxim.barybians.utils.isNull
import ru.maxim.barybians.utils.toast
import java.util.*
import kotlin.collections.ArrayList

class DialogActivity : BaseActivity(), DialogView {

    @InjectPresenter
    lateinit var dialogPresenter: DialogPresenter
    private var interlocutorId = 0
    private val messageItems = ArrayList<MessageItem>()
    private val binding by viewBinding(ActivityDialogBinding::bind)
    private val message = MutableLiveData<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interlocutorId = intent.getIntExtra("userId", 0)
        val userAvatar = intent.getStringExtra("userAvatar")
        val userName = intent.getStringExtra("userName")

        setContentView(R.layout.activity_dialog)

        // Setup actionbar
        setSupportActionBar(binding.dialogToolbar)
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setHomeButtonEnabled(false)
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        }
        with(binding.dialogToolbarUser) {
            this.userAvatar = userAvatar
            this.userName = userName

            root.setOnClickListener {
                val profileIntent =
                    Intent(this@DialogActivity, ProfileActivity::class.java).apply {
                        putExtra("userId", interlocutorId)
                    }
                startActivity(profileIntent)
            }
        }

        with(binding) {
            messageText = message
            lifecycleOwner = this@DialogActivity

            dialogMessageSendBtn.setOnClickListener {
                if (message.value.isNotNullOrBlank()) sendMessage(message.value!!)
            }

            dialogMessageEmojiBtn.setOnClickListener {
                val stickersPicker = StickersPickerDialog.newInstance {
                    // TODO: Implement sticker sending
                    toast(it)
                }
                stickersPicker.show(supportFragmentManager, "StickersPicker")
            }

            dialogBackBtn.setOnClickListener { finish() }
        }

        if (savedInstanceState.isNull()) { dialogPresenter.loadMessages(interlocutorId) }
    }

    override fun showMessages(messages: ArrayList<Message>) {
        binding.dialogLoading.visibility = GONE
        val currentUserId = PreferencesManager.userId
        messageItems.clear()
        messageItems.addAll(messages.map {
            val time = DateFormatUtils.getTime(it.time * 1000)
            val viewHolderId = "${it.text}${it.time}".hashCode().toLong()
            val status = if (it.unread == 1) Unread else Read
            return@map if (it.senderId == currentUserId)
                OutgoingMessage(viewHolderId, it.text, time, status)
            else
                IncomingMessage(viewHolderId, it.text, time, it.senderId)
        })
        binding.dialogRecyclerView.apply {
            adapter = DialogRecyclerAdapter(messageItems)
            scrollToPosition(messageItems.size - 1)
        }
    }

    private fun sendMessage(text: String) {
        val timestamp = Date().time
        val time = DateFormatUtils.getTime(timestamp)
        val viewHolderId = "${text}${timestamp}".hashCode().toLong()
        messageItems.add(OutgoingMessage(viewHolderId, text, time, Sending))
        binding.dialogRecyclerView.apply {
            adapter?.notifyItemInserted(messageItems.size - 1)
            smoothScrollToPosition(messageItems.size - 1)
        }
        message.postValue(null)
        dialogPresenter.sendMessage(interlocutorId, text, viewHolderId)
    }

    override fun onLoadingMessagesError() {
        binding.dialogLoading.visibility = GONE
        toast(R.string.an_error_occurred_while_loading_messages)
    }

    override fun showNoInternet() {
        binding.dialogLoading.visibility = GONE
        toast(R.string.no_internet_connection)
    }

    override fun onMessageSent(text: String, messageId: Long) {
        (binding.dialogRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)
            ?.setUnreadLabel()
    }

    override fun onMessageSendingError(messageId: Long) {
        (binding.dialogRecyclerView.findViewHolderForItemId(messageId) as? OutgoingMessageViewHolder)
            ?.setErrorLabel()
    }

    override fun onMessagesReceived(messages: ArrayList<Message>) {
        messages.forEach {
            when (it.senderId) {
                interlocutorId -> {
                    val time = DateFormatUtils.getTime(it.time)
                    val viewHolderId = "${it.text}${it.time}".hashCode().toLong()
                    messageItems.add(IncomingMessage(viewHolderId, it.text, time, it.senderId))
                    binding.dialogRecyclerView.adapter?.notifyItemInserted(messageItems.size)
                }
                PreferencesManager.userId -> {

                }
            }
        }
        binding.dialogRecyclerView.apply {
            val lastVisibleItemPosition =
                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            if (lastVisibleItemPosition >= messageItems.size - 2) {
                smoothScrollToPosition(messageItems.size - 1)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        dialogPresenter.stopObserving()
    }
}