package ru.maxim.barybians.data.network.response

import com.google.gson.annotations.SerializedName
import ru.maxim.barybians.domain.model.Attachment

data class SendMessageResponse(
    val user: MessageResponse
) {
    data class MessageResponse(
        val id: Int,
        val senderId: Int,
        val receiverId: Int,
        val text: String,
        @SerializedName("utime")
        val time: Long,
        val unread: Int,
        val attachments: List<Attachment>?
    )
}
