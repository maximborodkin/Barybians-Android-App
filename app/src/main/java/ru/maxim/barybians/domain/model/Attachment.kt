package ru.maxim.barybians.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ru.maxim.barybians.R
import ru.maxim.barybians.domain.model.Attachment.AttachmentType
import ru.maxim.barybians.domain.model.Attachment.FileAttachmentType.*
import ru.maxim.barybians.domain.model.Attachment.StyledAttachmentType

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
data class Attachment constructor(
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
) : Parcelable {

    constructor(parcel: Parcel) : this(
        attachmentId = parcel.readInt(),
        type = AttachmentType.values().firstOrNull { it.messageValue == parcel.readString() }
            ?: throw IllegalArgumentException("Unable to parse attachment"),
        style = StyledAttachmentType.values().firstOrNull { it.messageValue == parcel.readString() },
        parcel.readString(),
        sticker = parcel.readValue(Int::class.java.classLoader) as? Int,
        length = parcel.readInt(),
        offset = parcel.readInt(),
        url = parcel.readString(),
        title = parcel.readString(),
        fileSize = parcel.readValue(Long::class.java.classLoader) as? Long,
        extension = parcel.readString(),
        description = parcel.readString(),
        image = parcel.readString(),
        favicon = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(attachmentId)
        parcel.writeString(type.messageValue)
        parcel.writeString(style?.messageValue)
        parcel.writeString(pack)
        parcel.writeValue(sticker)
        parcel.writeInt(length)
        parcel.writeInt(offset)
        parcel.writeString(url)
        parcel.writeString(title)
        parcel.writeValue(fileSize)
        parcel.writeString(extension)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeString(favicon)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Attachment> {
            override fun createFromParcel(parcel: Parcel): Attachment {
                return Attachment(parcel)
            }

            override fun newArray(size: Int): Array<Attachment?> {
                return arrayOfNulls(size)
            }
        }

        fun resolveFileAttachmentType(extension: String?): FileAttachmentType =
            when (extension?.lowercase()) {
                in ARCHIVE.extensions.map { it.lowercase() } -> ARCHIVE
                in AUDIO.extensions.map { it.lowercase() } -> AUDIO
                in EXECUTABLE.extensions.map { it.lowercase() } -> EXECUTABLE
                in PDF.extensions.map { it.lowercase() } -> PDF
                in TEXT.extensions.map { it.lowercase() } -> TEXT
                else -> UNKNOWN
            }
    }

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

    enum class FileAttachmentType(
        val extensions: List<String>,
        @ColorRes val colorResource: Int,
        @DrawableRes val drawableResource: Int
    ) {
        ARCHIVE(
            extensions = listOf("rar", "zip", "iso", "tar", "gz", "7z"),
            colorResource = R.color.fileAttachmentArchive,
            drawableResource = R.drawable.ic_archive
        ),
        AUDIO(
            extensions = listOf("mp3", "flac", "aac", "m4a", "ogg", "wav"),
            colorResource = R.color.fileAttachmentAudio,
            drawableResource = R.drawable.ic_audio
        ),
        EXECUTABLE(
            extensions = listOf("exe", "apk", "bat", "jar", "msi", "py", "deb", "dmg"),
            colorResource = R.color.fileAttachmentExecutable,
            drawableResource = R.drawable.ic_application
        ),
        PDF(
            extensions = listOf("pdf"),
            colorResource = R.color.fileAttachmentPdf,
            drawableResource = R.drawable.ic_pdf
        ),
        TEXT(
            extensions = listOf("txt", "doc", "docx", "rtf", "tex", "wpd"),
            colorResource = R.color.fileAttachmentText,
            drawableResource = R.drawable.ic_text_snippet
        ),
        UNKNOWN(
            extensions = emptyList(),
            colorResource = R.color.fileAttachmentUnknown,
            drawableResource = R.drawable.ic_file
        )
    }
}