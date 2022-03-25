package ru.maxim.barybians.domain.model

/**
 * This class represents an attachment that may be in [Post], [Comment], [Message] in the **attachments** array.
 * There is five types of attachments described in the [AttachmentType] enum.
 *
 * [AttachmentType.STYLED] is styled text in one of four styles from the [StyledAttachmentType] enum.
 * **Fields**: *style*, *length*, *offset*.
 *
 * [AttachmentType.IMAGE] is a link to an image. **Fields**: *url*, *length*, *offset*.
 *
 * [AttachmentType.STICKER] is a representation of single sticker image. Any text or other attachments in given post,
 * comment or message should be ignored. Message with this attachment must be without other attachments and with text
 * of pattern **$[]()** where in first parentheses a pack and a sticker number in second. **Fields**: *url*, *pack*,
 * *length*, *offset*, *sticker*.
 *
 * [AttachmentType.LINK] is a representation of ordinary link to any resource in the internet except image and file.
 * For image used [AttachmentType.IMAGE] and for file [AttachmentType.FILE]. **Fields**: *url*, *image*, *title*,
 * *length*, *offset*, *favicon*, *description*.
 *
 * [AttachmentType.FILE] is a link to a file. **Fields**: *url*, *title*, *length*, *offset*, *fileSize*, *extension*.
 * */
data class Attachment(
    val attachmentId: Int,
    val type: AttachmentType,
    val style: StyledAttachmentType? = null,
    val pack: String? = null,
    val sticker: Int? = null,
    val length: Int,
    val offset: Int,
    val url: String? = null,
    val title: String? = null,
    val fileSize: Long? = null,
    val extension: String? = null,
    val description: String? = null,
    val image: String? = null,
    val favicon: String? = null,
) {

    enum class AttachmentType(val messageValue: String) {
        STYLED("styled"),
        IMAGE("image"),
        STICKER("sticker"),
        LINK("link"),
        FILE("file-link")
    }

    enum class StyledAttachmentType(val messageValue: String) {
        BOLD("bold"),
        ITALIC("italic"),
        UNDERLINE("underline"),
        STRIKE("strike")
    }
}