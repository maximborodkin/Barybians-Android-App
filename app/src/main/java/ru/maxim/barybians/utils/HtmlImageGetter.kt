package ru.maxim.barybians.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maxim.barybians.R
import kotlin.math.roundToInt

class HtmlImageGetter(private val scope: LifecycleCoroutineScope, private val res: Resources,
                      private val glide: RequestManager, private val targetTextView: TextView) : Html.ImageGetter {

    override fun getDrawable(url: String): Drawable {
        val holder = BitmapDrawablePlaceHolder(res, null)

        scope.launch(Dispatchers.IO) {
            runCatching {
                val bitmap = glide
                    .asBitmap()
                    .load(url)
                    .error(R.drawable.avatar)
                    .submit()
                    .get()
                val drawable = BitmapDrawable(res, bitmap)

                targetTextView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val width = targetTextView.width
                val ratio = width/drawable.intrinsicWidth.toFloat()
                val height = (drawable.intrinsicHeight * ratio).roundToInt()

                drawable.setBounds(0, 0, width, height)
                holder.setDrawable(drawable)
                holder.setBounds(0, 0, width, height)

                withContext(Dispatchers.Main) { targetTextView.text = targetTextView.text }
            }
        }

        return holder
    }

    internal class BitmapDrawablePlaceHolder(res: Resources, bitmap: Bitmap?) : BitmapDrawable(res, bitmap) {
        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.run { draw(canvas) }
        }

        fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
        }
    }
}