package ru.maxim.barybians.ui.dialog.stickerPicker

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import ru.maxim.barybians.data.network.NetworkManager
import ru.maxim.barybians.databinding.DialogStickerPickerBinding
import ru.maxim.barybians.domain.model.StickerPack
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.dpToPx
import ru.maxim.barybians.utils.load
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class StickersPickerDialog : BottomSheetDialogFragment() {

    private var binding: DialogStickerPickerBinding by notNull()

    @Inject
    lateinit var factory: StickerPickerViewModel.StickerPickerViewModelFactory
    private val model: StickerPickerViewModel by viewModels { factory }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogStickerPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.getStickers().collect(::renderTabs)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessage.observe(viewLifecycleOwner) { errorMessage -> context.toast(errorMessage) }
        }
    }

    private fun renderTabs(stickerPacks: List<StickerPack>) = with(binding) {
        stickerPickerTabLayout.removeAllTabs()
        stickerPickerTabLayout.tabGravity = TabLayout.GRAVITY_FILL
        stickerPacks.forEach { pack ->
            val tab = stickerPickerTabLayout.newTab()
            tab.tag = pack
            tab.customView = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                Glide.with(this)
                    .load("${NetworkManager.STICKERS_BASE_URL}/${pack.pack}/${pack.icon}.png")
                    .apply(RequestOptions().override(100, 100))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            tab.customView = null
                            tab.text = pack.name
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ) = false
                    })
                    .into(this)
            }
            stickerPickerTabLayout.addTab(tab)
        }
        stickerPickerTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { (tab?.tag as? StickerPack)?.let(::renderStickerPack) }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        if (stickerPickerTabLayout.tabCount > 0) {
            stickerPickerTabLayout.selectTab(stickerPickerTabLayout.getTabAt(0))
            (stickerPickerTabLayout.getTabAt(0)?.tag as? StickerPack)?.let(::renderStickerPack)
        }
    }

    private fun renderStickerPack(pack: StickerPack) = with(binding) {
        stickerPickerScrollView.fullScroll(View.FOCUS_LEFT)
        stickerPickerHolder.removeAllViews()
        for (sticker in 1..pack.amount) {
            val imageView = ImageView(context).apply {
                val imageSize = dpToPx(context.resources, 100)
                layoutParams = LinearLayoutCompat.LayoutParams(imageSize, imageSize)
                setPadding(imageSize / 20)
                val stickerUrl = "${NetworkManager.STICKERS_BASE_URL}/${pack.pack}/$sticker.png"
                load(stickerUrl)
                setOnClickListener {
                    setFragmentResult(
                        requestKey = stickerPickerResultKey,
                        result = bundleOf(stickerPackKey to pack.pack, stickerKey to sticker.toString())
                    )
                    dismiss()
                }
            }
            stickerPickerHolder.addView(imageView)
        }
    }

    companion object {
        const val stickerPickerResultKey = "sticker_picker_result"
        const val stickerPackKey = "sticker_pack"
        const val stickerKey = "sticker"
    }
}