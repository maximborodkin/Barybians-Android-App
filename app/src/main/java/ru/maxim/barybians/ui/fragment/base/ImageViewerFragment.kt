package ru.maxim.barybians.ui.fragment.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.view.ScaleImageView
import ru.maxim.barybians.utils.isNotNullOrBlank
import ru.maxim.barybians.utils.toast

class ImageViewerFragment : AppCompatDialogFragment() {

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
        val progressBar = ProgressBar(requireContext()).apply {
            isIndeterminate = true
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                .apply { addRule(RelativeLayout.CENTER_IN_PARENT) }
        }
        when {
            drawable != null -> imageView.setImageDrawable(drawable)
            imageUrl.isNotNullOrBlank() -> {
                parentContainer.addView(progressBar)
                Glide.with(requireContext())
                    .load(imageUrl)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            drawable = resource
                            progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(imageView)
            }
            else -> context?.toast(R.string.unable_to_load_image)
        }
        return parentContainer
    }

    companion object {
        private var drawable: Drawable? = null
        private var imageUrl: String? = null

        fun newInstance(drawable: Drawable? = null, imageUrl: String? = null): ImageViewerFragment {
            this.drawable = drawable
            this.imageUrl = imageUrl
            return ImageViewerFragment()
        }
    }
}