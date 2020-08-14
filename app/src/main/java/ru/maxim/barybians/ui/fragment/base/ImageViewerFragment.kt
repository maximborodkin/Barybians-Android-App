package ru.maxim.barybians.ui.fragment.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.maxim.barybians.ui.view.ScaleImageView
import ru.maxim.barybians.utils.isNotNull
import ru.maxim.barybians.utils.isNull

class ImageViewerFragment : AppCompatDialogFragment() {

    @IdRes
    private var imageViewId = View.generateViewId()

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