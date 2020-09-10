package ru.maxim.barybians.ui.fragment.dialogsList

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_dialog.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Dialog
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.fragment.dialogsList.DialogsListRecyclerAdapter.DialogViewHolder
import ru.maxim.barybians.ui.view.AvatarView
import ru.maxim.barybians.utils.DateFormatUtils

class DialogsListRecyclerAdapter(
    private val dialogs: ArrayList<Dialog>,
    private val onDialogClick: (userId: Int, userAvatar: String?, userName: String) -> Unit
) : RecyclerView.Adapter<DialogViewHolder>() {

    class DialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarView: AvatarView = view.itemDialogAvatar
        val nameView: TextView = view.itemDialogName
        val messageView: AppCompatTextView = view.itemDialogMessage
        val dateView: TextView = view.itemDialogDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder =
        DialogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dialog, parent, false))

    override fun getItemCount(): Int = dialogs.size

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        val context = holder.itemView.context
        val dialog = dialogs[position]
        Glide.with(context).load(dialog.secondUser.getAvatarUrl()).into(holder.avatarView)
        val interlocutorName = "${dialog.secondUser.firstName} ${dialog.secondUser.lastName}"
        holder.nameView.text = interlocutorName
        val lastMessageSpan =  SpannableStringBuilder()
        if (dialog.lastMessage.senderId == PreferencesManager.userId){
            val youString = context.getString(R.string.you)
            lastMessageSpan.append(youString)
            lastMessageSpan.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                youString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else{
            val interlocutorFirstName = dialog.secondUser.firstName
            lastMessageSpan.append(interlocutorFirstName)
            lastMessageSpan.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                interlocutorFirstName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        lastMessageSpan.append(": ${dialog.lastMessage.text}")
        val messagePreviewText = lastMessageSpan.toString()
        holder.messageView.text = messagePreviewText
        holder.dateView.text = DateFormatUtils.getSimplifiedDate(dialog.lastMessage.time*1000)
        holder.itemView.setOnClickListener { onDialogClick(
            dialog.secondUser.id,
            dialog.secondUser.getAvatarUrl(),
            interlocutorName
        ) }
    }
}