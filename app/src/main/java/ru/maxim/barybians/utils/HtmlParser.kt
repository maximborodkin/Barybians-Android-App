package ru.maxim.barybians.utils

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.ui.fragment.profile.OnImageClickListener
import java.lang.ref.WeakReference

class HtmlParser(private val scope: CoroutineScope,
                 private val resources: Resources,
                 private val requestManager: RequestManager
) {

    fun provideFormattedText(
        rawHtml: String,
        context: WeakReference<Context>,
        targetTextView: WeakReference<TextView>,
        targetImageLayout: WeakReference<ViewGroup>,
        onImageClickListener: OnImageClickListener
    ) {
        if (targetTextView.isNull() || targetImageLayout.isNull()) return
        if (rawHtml.contains("<img")) {
            val regex = Regex("<img.*?src=\"(.*?)\".*?>")
            val matches = regex.findAll(rawHtml)
            val images = ArrayList<Image>()
            matches.forEach {
                val isSticker = it.value.contains("class=\"sticker\"")
                val url = (if (isSticker) "${RetrofitClient.BASE_URL}/" else "") + it.groupValues[1]
                images.add(Image(it.value, url, isSticker))
            }

            val marginVertical = dpToPx(resources, 2)
            val params = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                .apply { setMargins(0, marginVertical, 0, marginVertical) }

            var textWithoutImages = rawHtml
            for (image in images) {
                val imageView = ImageView(context.get()).apply { layoutParams = params }
                targetImageLayout.get()?.addView(imageView)

                scope.launch {
                    requestManager
                        .load(image.url)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(imageView)
                    imageView.setOnClickListener { onImageClickListener.onImageClick(imageView.drawable) }
                }
                textWithoutImages = textWithoutImages.replace(image.tag, "")
            }
            targetTextView.get()?.text = HtmlCompat.fromHtml(textWithoutImages, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            targetTextView.get()?.text = HtmlCompat.fromHtml(rawHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

    data class Image(val tag: String, val url: String, val isSticker: Boolean = false)
}