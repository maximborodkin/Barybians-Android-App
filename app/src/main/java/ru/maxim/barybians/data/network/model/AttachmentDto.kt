package ru.maxim.barybians.data.network.model

data class AttachmentDto(
    val attachmentId: Int,
    val type: String,
    val style: String?,
    val pack: String?,
    val sticker: Int?,
    val length: Int,
    val offset: Int,
    val url: String?,
    val title: String?,
    val fileSize: Long?,
    val extension: String?,
    val description: String?,
    val image: String?,
    val favicon: String?,
)