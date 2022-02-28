package ru.maxim.barybians.ui.dialog.stickerPicker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.databinding.DialogStickerPickerBinding
import ru.maxim.barybians.domain.model.StickerPack
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import kotlin.properties.Delegates.notNull


class StickersPickerDialog : BottomSheetDialogFragment() {

    private var binding: DialogStickerPickerBinding by notNull()

    @Inject
    lateinit var factory: StickerPickerViewModel.StickerPickerViewModelFactory
    private val model: StickerPickerViewModel by viewModels { factory }

    private var onPickSticker: ((stickerUrl: String) -> Unit)? = null

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
        stickerPacks.forEach { stickerPack ->
            val tab = stickerPickerTabLayout.newTab()
            tab.tag = stickerPack
            tab.customView = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                Glide.with(this)
                    .load("${RetrofitClient.BASE_URL}img/stickers-png/${stickerPack.pack}/${stickerPack.icon}")
                    .apply(RequestOptions().override(100, 100))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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
        stickerPickerHolder.removeAllViews()
        for (sticker in 1..pack.amount) {
            val imageView = ImageView(context).apply {
                val imageSize = stickerPickerHolder.width / 4
                layoutParams = LinearLayoutCompat.LayoutParams(imageSize, imageSize)
                setPadding(imageSize / 20)
                val stickerUrl = "${RetrofitClient.BASE_URL}img/stickers-png/${pack.name}/$sticker.png"
                Glide.with(this).load(stickerUrl).into(this)
                setOnClickListener { onPickSticker?.invoke(stickerUrl) }
            }
            stickerPickerHolder.addView(imageView)
        }
    }

    fun setOnPickListener(listener: ((stickerUrl: String) -> Unit)?) {
        onPickSticker = listener
    }

//    private fun loadTabs() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val packs = chatService.getStickersPacks()
//            CoroutineScope(Dispatchers.Main).launch {
//                if (packs.isSuccessful && packs.body() != null) {
//                    packs.body()?.forEach { pack ->
//                        val imageUrl =
//                            "${RetrofitClient.BASE_URL}img/stickers-png/${pack.pack}/${pack.icon}"
//                        val tab = binding.stickersPickerTabLayout.newTab()
//                            .setCustomView(createTabItemView(imageUrl))
//                        tab.tag = pack.pack
//                        binding.stickersPickerTabLayout.addTab(tab)
//                    }
//                    binding.stickersPickerTabLayout.tabGravity = TabLayout.GRAVITY_FILL
//                    binding.stickersPickerTabLayout.addOnTabSelectedListener(object :
//                        TabLayout.OnTabSelectedListener {
//                        override fun onTabSelected(tab: TabLayout.Tab) {
//                            loadStickerPack(tab.tag.toString())
//                        }
//
//                        override fun onTabUnselected(tab: TabLayout.Tab?) {}
//                        override fun onTabReselected(tab: TabLayout.Tab?) {}
//                    })
//                    loadStickerPack(binding.stickersPickerTabLayout.getTabAt(0)?.tag.toString())
//                } else {
//                    context.toast("Unable to load stickers")
//                }
//            }
//        }
//    }
//
//    private fun loadStickerPack(packName: String) {
//        val stickers = ArrayList<String>()
//        for (i in 1..20) {
//            stickers.add("${RetrofitClient.BASE_URL}img/stickers-png/${packName}/${i}.png")
//        }
//        binding.stickersPickerRecycler.layoutManager = GridLayoutManager(context, 4)
//        binding.stickersPickerRecycler.adapter = StickerPickerRecyclerAdapter(stickers) { position ->
//            onStickerClick("${RetrofitClient.BASE_URL}img/stickers-png/${packName}/${position + 1}.png")
//        }
//    }
//
//    private fun createTabItemView(imgUri: String): View {
//        val imageView = ImageView(context)
//        imageView.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//        Glide.with(this)
//            .load(imgUri)
//            .apply(RequestOptions().override(100, 100))
//            .thumbnail(0.2F)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(imageView)
//        return imageView
//    }
}