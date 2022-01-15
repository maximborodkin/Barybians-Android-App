package ru.maxim.barybians.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.Matrix.*
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_UP
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ScaleGestureDetectorCompat

/**
 * ScaleImageView is a extension of [AppCompatImageView], providing a pinch-to-zoom and
 * double tap gestures to scaling image and swipe to top or bottom to dismiss.
 */
class ScaleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle), OnScaleGestureListener {

    private val resetAnimationDuration = 200L
    private var startMatrix = Matrix()
    private val currentMatrix = Matrix()
    private val matrixValues = FloatArray(9)
    private var startValues: FloatArray? = null
    private val minScale = 0.5F
    private val maxScale = 10F
    private var calculatedMinScale = minScale
    private var calculatedMaxScale = maxScale
    private val bounds = RectF()
    private var doubleTapToZoomScaleFactor = 3f
    private val last = PointF(0F, 0F)
    private var startScale = 1f
    private var scaleBy = 1f
    private var currentScaleFactor = 1f
    private var previousPointerCount = 1
    private var currentPointerCount = 0
    private var scaleDetector: ScaleGestureDetector = ScaleGestureDetector(context, this).apply {
        ScaleGestureDetectorCompat.setQuickScaleEnabled(this, false)
    }
    private var resetAnimator: ValueAnimator? = null
    private var doubleTapDetected = false
    private var singleTapDetected = false
    private val gestureListener: GestureDetector.OnGestureListener =
        object : SimpleOnGestureListener() {
            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                if (e.action == ACTION_UP) {
                    doubleTapDetected = true
                }
                singleTapDetected = false
                return false
            }

            override fun onSingleTapUp(event: MotionEvent): Boolean {
                if (event.x !in bounds.left..bounds.right || event.y !in bounds.top..bounds.bottom)
                    dismissView()
                singleTapDetected = true
                return false
            }

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                singleTapDetected = false
                return false
            }

            override fun onDown(e: MotionEvent) = true
        }
    private var gestureDetector: GestureDetector = GestureDetector(context, gestureListener)
    var onDismissListener: () -> Unit? = {}

    private fun updateBounds(values: FloatArray) {
        if (drawable != null) {
            bounds[values[MTRANS_X], values[MTRANS_Y], drawable.intrinsicWidth * values[MSCALE_X] + values[MTRANS_X]] =
                drawable.intrinsicHeight * values[MSCALE_Y] + values[MTRANS_Y]
        }
    }

    private val currentDisplayedWidth: Float
        get() = drawable?.intrinsicWidth?.times(matrixValues[MSCALE_X]) ?: 0F


    private val currentDisplayedHeight: Float
        get() = drawable?.intrinsicHeight?.times(matrixValues[MSCALE_Y]) ?: 0F

    private fun setStartValues() {
        startValues = FloatArray(9)
        startMatrix = Matrix(imageMatrix)
        startMatrix.getValues(startValues)
        calculatedMinScale = minScale * startValues!![MSCALE_X]
        calculatedMaxScale = maxScale * startValues!![MSCALE_X]
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (scaleType != ScaleType.MATRIX) {
            super.setScaleType(ScaleType.MATRIX)
        }

        if (startValues == null) {
            setStartValues()
        }

        currentPointerCount = event.pointerCount

        currentMatrix.set(imageMatrix)
        currentMatrix.getValues(matrixValues)
        updateBounds(matrixValues)
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        if (event.y !in bounds.top..bounds.bottom || event.x !in bounds.left..bounds.right) {
//            if (event.actionMasked == MotionEvent.ACTION_UP) dismissView()
            return true
        }

        if (doubleTapDetected) {
            doubleTapDetected = false
            singleTapDetected = false
            if (matrixValues[MSCALE_X] != startValues!![MSCALE_X]) {
                animateToStartMatrix()
            } else {
                val zoomMatrix = Matrix(currentMatrix)
                zoomMatrix.postScale(
                    doubleTapToZoomScaleFactor,
                    doubleTapToZoomScaleFactor,
                    scaleDetector.focusX,
                    scaleDetector.focusY
                )
                animateScaleAndTranslationToMatrix(zoomMatrix)
            }
            return true
        } else if (!singleTapDetected) {
            /* if the event is a down touch, or if the number of touch points changed,
             * we should reset our start point, as event origins have likely shifted to a
             * different part of the screen*/
            if (event.actionMasked == MotionEvent.ACTION_DOWN ||
                currentPointerCount != previousPointerCount
            ) {
                last[scaleDetector.focusX] = scaleDetector.focusY
            } else if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                val focusX = scaleDetector.focusX
                val focusY = scaleDetector.focusY
                //calculate the distance for translation
                val xDistance = getXDistance(focusX, last.x)
                val yDistance = getYDistance(focusY, last.y)
                currentMatrix.postTranslate(xDistance, yDistance)
                currentMatrix.postScale(scaleBy, scaleBy, focusX, focusY)
                currentScaleFactor =
                    matrixValues[MSCALE_X] / startValues!![MSCALE_X]
                imageMatrix = currentMatrix
                last[focusX] = focusY
            }
            if (event.actionMasked == ACTION_UP || event.actionMasked == ACTION_CANCEL) {
                scaleBy = 1f
                resetImage()
            }
        }
        parent.requestDisallowInterceptTouchEvent(disallowParentTouch())

        //this tracks whether they have changed the number of fingers down
        previousPointerCount = currentPointerCount
        performClick()
        return true
    }

    private fun disallowParentTouch() =
        /*currentPointerCount > 1 || */currentScaleFactor > 1.0f || isAnimating

    private val isAnimating: Boolean
        get() = resetAnimator?.isRunning ?: false

    private fun resetImage() {
        if (matrixValues[MSCALE_X] <= startValues!![MSCALE_X]) {
            animateToStartMatrix()
        } else {
            animateTranslationX()
            animateTranslationY()
        }
    }

    /**
     * This helps to keep the image on-screen by animating the translation to the nearest
     * edge, both vertically and horizontally.
     */

    /**
     * Reset image back to its starting size. If `animate` is false, image
     * will snap back to its original size.
     *
     */
    /**
     * Reset image back to its original size. Will snap back to original size
     * if animation on reset is disabled via [.setAnimateOnReset].
     */

    /**
     * Animate the matrix back to its original position after the user stopped interacting with it.
     */
    private fun animateToStartMatrix() {
        animateScaleAndTranslationToMatrix(startMatrix)
    }

    /**
     * Animate the scale and translation of the current matrix to the target
     * matrix.
     *
     * @param targetMatrix the target matrix to animate values to
     */
    private fun animateScaleAndTranslationToMatrix(targetMatrix: Matrix) {
        val targetValues = FloatArray(9)
        targetMatrix.getValues(targetValues)
        val beginMatrix = Matrix(imageMatrix)
        beginMatrix.getValues(matrixValues)

        //difference in current and original values
        val xSDiff = targetValues[MSCALE_X] - matrixValues[MSCALE_X]
        val ySDiff = targetValues[MSCALE_Y] - matrixValues[MSCALE_Y]
        val xTDiff = targetValues[MTRANS_X] - matrixValues[MTRANS_X]
        val yTDiff = targetValues[MTRANS_Y] - matrixValues[MTRANS_Y]
        resetAnimator = ValueAnimator.ofFloat(0f, 1f)
        resetAnimator?.addUpdateListener(object : AnimatorUpdateListener {
            val activeMatrix = Matrix(imageMatrix)
            val values = FloatArray(9)
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.animatedValue as Float
                activeMatrix.set(beginMatrix)
                activeMatrix.getValues(values)
                values[MTRANS_X] = values[MTRANS_X] + xTDiff * value
                values[MTRANS_Y] = values[MTRANS_Y] + yTDiff * value
                values[MSCALE_X] = values[MSCALE_X] + xSDiff * value
                values[MSCALE_Y] = values[MSCALE_Y] + ySDiff * value
                activeMatrix.setValues(values)
                imageMatrix = activeMatrix
            }
        })
        resetAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationEnd(animation: Animator) {
                imageMatrix = targetMatrix
            }

            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationRepeat(p0: Animator?) {}
        })
        resetAnimator?.duration = resetAnimationDuration
        resetAnimator?.start()
    }

    private fun animateTranslationX() {
        if (currentDisplayedWidth > width) {
            //the left edge is too far to the interior
            if (bounds.left > 0) {
                animateMatrixIndex(MTRANS_X, 0f)
            } else if (bounds.right < width) {
                animateMatrixIndex(MTRANS_X, bounds.left + width - bounds.right)
            }
        } else {
            //left edge needs to be pulled in, and should be considered before the right edge
            if (bounds.left < 0) {
                animateMatrixIndex(MTRANS_X, 0f)
            } else if (bounds.right > width) {
                animateMatrixIndex(MTRANS_X, bounds.left + width - bounds.right)
            }
        }
    }

    private fun animateTranslationY() {
        if (currentDisplayedHeight > height) {
            //the top edge is too far to the interior
            if (bounds.top > 0) {
                animateMatrixIndex(MTRANS_Y, 0f)
            } else if (bounds.bottom < height) {
                animateMatrixIndex(MTRANS_Y, bounds.top + height - bounds.bottom)
            }
        } else {
            //top needs to be pulled in, and needs to be considered before the bottom edge
            if (bounds.top < 0) {
                animateMatrixIndex(MTRANS_Y, 0f)
            } else if (bounds.bottom > height) {
                animateMatrixIndex(MTRANS_Y, bounds.top + height - bounds.bottom)
            }
        }
    }

    private fun animateMatrixIndex(index: Int, to: Float) {
        ValueAnimator.ofFloat(matrixValues[index], to).apply {
            addUpdateListener(object : AnimatorUpdateListener {
                val values = FloatArray(9)
                var current = Matrix()
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    current.set(imageMatrix)
                    current.getValues(values)
                    values[index] = animation.animatedValue as Float
                    current.setValues(values)
                    imageMatrix = current
                }
            })
            duration = resetAnimationDuration
            start()
        }
    }

    /**
     * Get the x distance to translate the current image.
     *
     * @param toX   the current x location of touch focus
     * @param fromX the last x location of touch focus
     * @return the distance to move the image,
     * will restrict the translation to keep the image on screen.
     */
    private fun getXDistance(toX: Float, fromX: Float): Float {
        var xDistance = toX - fromX

        xDistance = getRestrictedXDistance(xDistance)

        //prevents image from translating an infinite distance offscreen
        if (bounds.right + xDistance < 0) {
            xDistance = -bounds.right
        } else if (bounds.left + xDistance > width) {
            xDistance = width - bounds.left
        }
        return xDistance
    }

    /**
     * Get the horizontal distance to translate the current image, but restrict
     * it to the outer bounds of the [AppCompatImageView]. If the current
     * image is smaller than the bounds, keep it within the current bounds.
     * If it is larger, prevent its edges from translating farther inward
     * from the outer edge.
     *
     * @param xDistance the current desired horizontal distance to translate
     * @return the actual horizontal distance to translate with bounds restrictions
     */
    private fun getRestrictedXDistance(xDistance: Float): Float {
        var restrictedXDistance = xDistance
        if (currentDisplayedWidth >= width) {
            if (bounds.left <= 0 && bounds.left + xDistance > 0 && !scaleDetector.isInProgress) {
                restrictedXDistance = -bounds.left
            } else if (bounds.right >= width && bounds.right + xDistance < width && !scaleDetector.isInProgress) {
                restrictedXDistance = width - bounds.right
            }
        } else if (!scaleDetector.isInProgress) {
            if (bounds.left >= 0 && bounds.left + xDistance < 0) {
                restrictedXDistance = -bounds.left
            } else if (bounds.right <= width && bounds.right + xDistance > width) {
                restrictedXDistance = width - bounds.right
            }
        }
        return restrictedXDistance
    }

    /**
     * Get the y distance to translate the current image.
     *
     * @param toY   the current y location of touch focus
     * @param fromY the last y location of touch focus
     * @return the distance to move the image,
     * will restrict the translation to keep the image on screen.
     */
    private fun getYDistance(toY: Float, fromY: Float): Float {
        var yDistance = toY - fromY

        yDistance = getRestrictedYDistance(yDistance)

        //prevents image from translating an infinite distance offscreen
        if (bounds.bottom + yDistance < 0) {
            yDistance = -bounds.bottom
        } else if (bounds.top + yDistance > height) {
            yDistance = height - bounds.top
        }
        return yDistance
    }

    /**
     * Get the vertical distance to translate the current image, but restrict
     * it to the outer bounds of the [AppCompatImageView]. If the current
     * image is smaller than the bounds, keep it within the current bounds.
     * If it is larger, prevent its edges from translating farther inward
     * from the outer edge.
     *
     * @param yDistance the current desired vertical distance to translate
     * @return the actual vertical distance to translate with bounds restrictions
     */
    private fun getRestrictedYDistance(yDistance: Float): Float {
        var restrictedYDistance = yDistance
        if (currentDisplayedHeight >= height) {
            if (bounds.top <= 0 && bounds.top + yDistance > 0 && !scaleDetector.isInProgress) {
                restrictedYDistance = -bounds.top
            } else if (bounds.bottom >= height && bounds.bottom + yDistance < height && !scaleDetector.isInProgress) {
                restrictedYDistance = height - bounds.bottom
            }
        } else if (!scaleDetector.isInProgress) {
            if (bounds.top >= 0 && bounds.top + yDistance < 0) {
                dismissView()
                restrictedYDistance = -bounds.top
            } else if (bounds.bottom <= height && bounds.bottom + yDistance > height) {
                dismissView()
                restrictedYDistance = height - bounds.bottom
            }
        }
        return restrictedYDistance
    }

    private fun dismissView() {
        onDismissListener()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {

        //calculate value we should scale by, ultimately the scale will be startScale*scaleFactor
        scaleBy = startScale * detector.scaleFactor / matrixValues[MSCALE_X]

        //what the scaling should end up at after the transformation
        val projectedScale = scaleBy * matrixValues[MSCALE_X]

        //clamp to the min/max if it's going over
        if (projectedScale < calculatedMinScale) {
            scaleBy = calculatedMinScale / matrixValues[MSCALE_X]
        } else if (projectedScale > calculatedMaxScale) {
            scaleBy = calculatedMaxScale / matrixValues[MSCALE_X]
        }
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        startScale = matrixValues[MSCALE_X]
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        scaleBy = 1f
    }
}