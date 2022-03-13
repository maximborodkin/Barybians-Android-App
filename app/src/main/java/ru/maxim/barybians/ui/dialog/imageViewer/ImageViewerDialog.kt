package ru.maxim.barybians.ui.dialog.imageViewer

import android.Manifest
import android.app.DownloadManager
import android.app.DownloadManager.Request.NETWORK_MOBILE
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.FragmentImageViewerBinding
import ru.maxim.barybians.utils.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class ImageViewerDialog : AppCompatDialogFragment() {

    private val args: ImageViewerDialogArgs by navArgs()
    private var binding: FragmentImageViewerBinding by notNull()

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

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
        binding = FragmentImageViewerBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            imageUrl = args.imageUrl
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        if (preferencesManager.isDebug) context.longToast(args.imageUrl)
        imageViewer.onDismissListener = ::dismiss
        imageViewerDownloadButton.setOnClickListener { downloadImage(args.imageUrl) }
        imageViewerShareButton.setOnClickListener { shareImage() }
    }

    private fun downloadImage(imageUrl: String) {
        fun download() {
            val filename = imageUrl.split("/").lastOrNull() ?: "${date(Date())} ${time(Date())}"
            val downloadManager = (context?.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager) ?: return
            val request = DownloadManager.Request(Uri.parse(imageUrl))
            request.setAllowedNetworkTypes(NETWORK_WIFI or NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    DIRECTORY_PICTURES,
                    File.separator + getString(R.string.app_name) + File.separator + filename
                )
            downloadManager.enqueue(request)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            download()
        } else {
            Dexter
                .withContext(context)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) = download()

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) =
                        context.toast(getString(R.string.allow_media_access_to_download))

                    override fun onPermissionRationaleShouldBeShown(
                        request: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
        }
    }

    private fun shareImage() {
        fun share() {
            val drawable = (binding.imageViewer.drawable as? BitmapDrawable)?.bitmap ?: return shareImageUrl()
            val uri = getUriFromBitmap(drawable) ?: return shareImageUrl()
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/*"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            share()
        } else {
            Dexter
                .withContext(context)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) = share()

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        if (response?.isPermanentlyDenied == true) {
                            Snackbar
                                .make(binding.imageViewer, R.string.unable_to_share_images_without_media, LENGTH_LONG)
                                .setAction(R.string.share_url) { shareImageUrl() }
                                .show()
                        } else {
                            context.toast(getString(R.string.allow_media_access_to_share))
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        request: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
        }
    }

    @Suppress("DEPRECATION")
    private fun getUriFromBitmap(bitmap: Bitmap): Uri? {
        try {
            val file = File(
                context?.getExternalFilesDir(DIRECTORY_PICTURES),
                "share_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri.parse(MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap, null, null))
            } else {
                Uri.fromFile(file)
            }
        } catch (e: IOException) {
            if (preferencesManager.isDebug) context.longToast(e.message)
            return null
        }
    }

    private fun shareImageUrl() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, args.imageUrl)
            type = "text/plan"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image_url)))
    }
}