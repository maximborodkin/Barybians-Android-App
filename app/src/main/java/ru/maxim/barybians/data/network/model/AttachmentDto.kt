package ru.maxim.barybians.data.network.model

data class AttachmentDto(
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
    enum class AttachmentType(val messageValue: String) {
        STYLED("styled"),
        LINK("link"),
        IMAGE("image"),
        VIDEO("video"),
        STICKER("sticker")
    }

    enum class StyledAttachmentType(val messageValue: String) {
        BOLD("bold"),
        ITALIC("italic"),
        UNDERLINE("underline"),
        STRIKE("strike")
    }
}