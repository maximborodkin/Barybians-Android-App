package ru.maxim.barybians.utils

import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import ru.maxim.barybians.data.network.NetworkManager
import javax.inject.Inject

class HtmlUtils @Inject constructor() {

    /**
     * @param [rawHtml] is an HTML string
     * @return [Pair] of [Spanned] and [List] of urls for images in given HTML string
     * */
    fun parseHtml(rawHtml: String): Pair<Spanned, List<ImageAttachment>> {
        if (rawHtml.contains("<img")) {
            val regex = Regex("<img.*?src=\"(.*?)\".*?>")
            val matches = regex.findAll(rawHtml)

            var clearText = rawHtml
            val images = mutableListOf<ImageAttachment>()
            matches.forEach { match ->
                val isSticker = match.value.contains("class=\"sticker\"")
                val url = (if (isSticker) "${NetworkManager.STICKERS_BASE_URL}/" else "") + match.groupValues[1]
                images.add(ImageAttachment(url = url, isSticker = isSticker))
                clearText = clearText.replace(match.value, "")
            }

            val spanned = HtmlCompat.fromHtml(clearText, FROM_HTML_MODE_COMPACT)
            return Pair(spanned, images)
        } else {
            return Pair(HtmlCompat.fromHtml(rawHtml, FROM_HTML_MODE_COMPACT), listOf())
        }
    }

    class ImageAttachment(val url: String, val isSticker: Boolean)
}