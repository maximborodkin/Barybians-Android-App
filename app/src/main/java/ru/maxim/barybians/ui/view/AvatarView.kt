package ru.maxim.barybians.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import ru.maxim.barybians.R


class AvatarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    private var hasOnlineStatus: Boolean = true
    private var isOnline: Boolean = false

    private var maskPath: Path? = null
    private val maskPaint = Paint()
    private var cornerRadius = width/2

    init {
        getContext().obtainStyledAttributes(attrs, R.styleable.AvatarView, defStyleAttr, 0).apply {
            hasOnlineStatus = getBoolean(R.styleable.AvatarView_hasOnlineStatus, true)
            isOnline = getBoolean(R.styleable.AvatarView_isOnline, false)
            recycle()
        }

        maskPaint.apply {
            setLayerType(LAYER_TYPE_HARDWARE, null)
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            maskPaint.color = Color.TRANSPARENT
            isAntiAlias = true
        }
    }

    private val onlineStatusPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.online_circle_color)
    }

    override fun onDraw(canvas: Canvas?) {
        if(canvas?.isOpaque != false) {
            canvas?.saveLayerAlpha(0F, 0F, width.toFloat(), height.toFloat(), 255, Canvas.ALL_SAVE_FLAG)
        }
        super.onDraw(canvas)
        if(maskPath != null) {
            canvas?.drawPath(maskPath!!, maskPaint)
        }
        if (hasOnlineStatus and isOnline) {
            val radius = width/8.toFloat()
            canvas?.drawCircle(width-radius, height-radius, radius, onlineStatusPaint)
        }
    }

    private fun setCornerRadius(newCornerRadius: Int) {
        cornerRadius = newCornerRadius
        generateMaskPath(width, height)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        if (w != oldW || h != oldH) {
            setCornerRadius(w/2)
            generateMaskPath(w, h)
        }
    }

    private fun generateMaskPath(w: Int, h: Int) {
        maskPath = Path().apply {
            addRoundRect(
                RectF(0F, 0F, w.toFloat(), h.toFloat()),
                cornerRadius.toFloat(),
                cornerRadius.toFloat(),
                Path.Direction.CW
            )
            fillType = Path.FillType.INVERSE_WINDING
        }
    }
}