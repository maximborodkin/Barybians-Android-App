package ru.maxim.barybians.ui.view

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import ru.maxim.barybians.R


class ProfileBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var hasBlur: Boolean = true
    var blurRadius: Float = 18F
    var hasDarkForeground: Boolean = true
    var foregroundTransparency: Int = 160

    init {
        getContext().obtainStyledAttributes(
            attrs,
            R.styleable.ProfileBackgroundView,
            defStyleAttr,
            0
        ).apply {
            hasBlur = getBoolean(R.styleable.ProfileBackgroundView_hasBlur, true)
            blurRadius = getFloat(R.styleable.ProfileBackgroundView_blurRadius, 18F)
            hasDarkForeground =
                getBoolean(R.styleable.ProfileBackgroundView_hasDarkForeground, true)
            foregroundTransparency =
                getInt(R.styleable.ProfileBackgroundView_foregroundTransparency, 160)
            recycle()
        }
    }

    private val darkPaint: Paint = Paint().apply {
        if (foregroundTransparency < 0) foregroundTransparency = 0
        if (foregroundTransparency > 255) foregroundTransparency = 255
        color = Color.argb(foregroundTransparency, 0, 0, 0)
    }

    private var imageRect = Rect(left, top, top, bottom)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = Rect(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        if (drawable != null && matrix != null) {
            if (blurRadius < 0) blurRadius = 0F
            if (blurRadius > 25) blurRadius = 25F
            if (hasBlur)
                canvas?.drawBitmap(
                    blurRenderScript(context, drawable.toBitmap()),
                    imageRect,
                    imageRect,
                    null
                )
            else
                canvas?.drawBitmap(drawable.toBitmap(), imageRect, imageRect, null)
            if (hasDarkForeground)
                canvas?.drawRect(imageRect, darkPaint)
        }
    }

    private fun blurRenderScript(context: Context?, smallBitmap: Bitmap): Bitmap {
        val bitmap = Bitmap.createBitmap(
            smallBitmap.width,
            smallBitmap.height + translationY.toInt(),
            Bitmap.Config.ARGB_8888
        )
        val renderScript = RenderScript.create(context)
        val blurInput = Allocation.createFromBitmap(renderScript, smallBitmap)
        val blurOutput = Allocation.createFromBitmap(renderScript, bitmap)
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).apply {
            setInput(blurInput)
            setRadius(blurRadius)
            forEach(blurOutput)
        }
        blurOutput.copyTo(bitmap)
        renderScript.destroy()
        return bitmap
    }
}