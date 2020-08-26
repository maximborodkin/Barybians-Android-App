package ru.maxim.barybians.ui.fragment.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.maxim.barybians.ui.view.ScaleImageView
import ru.maxim.barybians.utils.isNotNull

class ImageViewerFragment : AppCompatDialogFragment() {

    init {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    }

    override fun onStart() {
        super.onStart()
        retainInstance = true
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imageView = ScaleImageView(requireContext())
        imageView.onDismissListener = {
            dismiss()
        }
        val params = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        imageView.layoutParams = params
        if (drawable != null) {
            imageView.setImageDrawable(drawable)
        } else if (imageUrl.isNotNull()) {
            Glide.with(requireContext())
                .load(imageUrl)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(e: GlideException?,
                                              model: Any?,
                                              target: Target<Drawable>?,
                                              isFirstResource: Boolean
                    ): Boolean = false

                    override fun onResourceReady(resource: Drawable?,
                                                 model: Any?,
                                                 target: Target<Drawable>?,
                                                 dataSource: DataSource?,
                                                 isFirstResource: Boolean
                    ): Boolean {
                        drawable = resource
                        return false
                    }
                })
                .into(imageView)
        }
        return imageView
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