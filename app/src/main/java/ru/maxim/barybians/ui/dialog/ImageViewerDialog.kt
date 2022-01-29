package ru.maxim.barybians.ui.dialog

import android.graphics.Bitmap
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
import ru.maxim.barybians.ui.view.ScaleImageView
import ru.maxim.barybians.utils.isNotNullOrBlank
import ru.maxim.barybians.utils.toast

class ImageViewerDialog : AppCompatDialogFragment() {

    private val args: ImageViewerDialogArgs by navArgs()

    init {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val parentContainer = RelativeLayout(requireContext())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imageView = ScaleImageView(requireContext()).apply {
            onDismissListener = { dismiss() }
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            parentContainer.addView(this)
        }

        val imageUrl: String? = args.imageUrl
        val imageBitmap: Bitmap? = args.imageBitmap

        when {
            imageBitmap != null -> imageView.setImageBitmap(imageBitmap)
            imageUrl.isNotNullOrBlank() -> {
                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(CircularProgressDrawable(requireContext()).apply {
                        strokeWidth = 3F
                        centerRadius = 64F
                        start()
                    })
                    .error(R.drawable.ic_broken_image)
                    .into(imageView)
            }
            else -> {
                imageView.setImageResource(R.drawable.ic_broken_image)
                context?.toast(R.string.unable_to_load_image)
            }
        }
        return parentContainer
    }
}