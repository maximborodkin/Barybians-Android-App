package ru.maxim.barybians.utils

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

@Suppress("UNCHECKED_CAST")
class SwipeDismissCallback<VH : ViewHolder>(
    private val swipeBackground: ColorDrawable,
    private val iconDrawable: Drawable,
    private val allowSwipe: (viewHolder: VH) -> Boolean,
    private val onSwiped: (viewHolder: VH) -> Unit
) : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder) =
        false

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val vh = viewHolder as? VH
            ?: throw IllegalStateException("Unexpected ViewHolder type ${viewHolder::class.simpleName}")
        onSwiped(vh)
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        val vh = viewHolder as? VH
            ?: throw IllegalStateException("Unexpected ViewHolder type ${viewHolder::class.simpleName}")
        return if (allowSwipe(vh)) Callback.makeMovementFlags(0, LEFT or RIGHT) else 0
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - iconDrawable.intrinsicHeight) / 2

        if (dX > 0) {
            swipeBackground.setBounds(
                itemView.left,
                itemView.top,
                dX.toInt(),
                itemView.bottom
            )
            iconDrawable.setBounds(
                itemView.left + iconMargin,
                itemView.top + iconMargin,
                itemView.left + iconMargin + iconDrawable.intrinsicWidth,
                itemView.bottom - iconMargin
            )
        } else {
            swipeBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            iconDrawable.setBounds(
                itemView.right - iconMargin - iconDrawable.intrinsicWidth,
                itemView.top + iconMargin,
                itemView.right - iconMargin,
                itemView.bottom - iconMargin
            )
        }
        swipeBackground.draw(c)
        iconDrawable.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}