package ru.maxim.barybians.domain.model

data class Attachment(
    val messageId: Int,
    val type: String
//    val offset: Int?,
//    val length: Int?,
//    val style: String?,
//
//    val url: String?,
//    val title: String?,
//    val description: String?,
//    val image: String?,
//    val favicon: String?,
//    val timestamp: Long?
) {
    enum class AttachmentType {
        STYLED, LINK, IMAGE, VIDEO
    }

    enum class StyledAttachmentType {
        BOLD, ITALIC, UNDERLINE, STRIKE
    }
}