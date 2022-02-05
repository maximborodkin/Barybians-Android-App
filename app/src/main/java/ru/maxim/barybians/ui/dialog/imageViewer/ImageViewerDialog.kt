package ru.maxim.barybians.ui.dialog.imageViewer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import ru.maxim.barybians.R

class ImageViewerDialog : AppCompatDialogFragment() {

    private val args: ImageViewerDialogArgs by navArgs()

    override fun onStart() {
        super.onStart()
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.argb(180, 0, 0, 0)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val parentContainer = RelativeLayout(requireContext())
        val imageView = ScaleImageView(parentContainer.context).apply {
            // Dismiss a fragment when the ScaleImageView has been dismissed
            // by the user (usually a vertical swipe)
            onDismissListener = ::dismiss
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
        parentContainer.addView(imageView)

        Glide.with(parentContainer.context)
            .load(args.imageUrl)
            .placeholder(CircularProgressDrawable(parentContainer.context).apply {
                setColorSchemeColors(Color.WHITE)
                strokeWidth = 3F
                centerRadius = 64F
                start()
            })
            .error(R.drawable.ic_broken_image)
            .into(imageView)

        return parentContainer
    }
}