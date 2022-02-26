package ru.maxim.barybians.ui.fragment.stickerPicker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.databinding.FragmentStickersPickerBinding
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject
import kotlin.properties.Delegates.notNull


class StickersPickerDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var chatService: ChatService

    private var binding: FragmentStickersPickerBinding by notNull()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickersPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTabs()
    }

    private fun loadTabs() {
        CoroutineScope(Dispatchers.IO).launch {
            val packs = chatService.getStickersPacks()
            CoroutineScope(Dispatchers.Main).launch {
                if (packs.isSuccessful && packs.body() != null) {
                    packs.body()?.forEach { pack ->
                        val imageUrl =
                            "${RetrofitClient.BASE_URL}img/stickers-png/${pack.pack}/${pack.icon}"
                        val tab = binding.stickersPickerTabLayout.newTab()
                            .setCustomView(createTabItemView(imageUrl))
                        tab.tag = pack.pack
                        binding.stickersPickerTabLayout.addTab(tab)
                    }
                    binding.stickersPickerTabLayout.tabGravity = TabLayout.GRAVITY_FILL
                    binding.stickersPickerTabLayout.addOnTabSelectedListener(object :
                        TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab) {
                            loadStickerPack(tab.tag.toString())
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab?) {}
                        override fun onTabReselected(tab: TabLayout.Tab?) {}
                    })
                    loadStickerPack(binding.stickersPickerTabLayout.getTabAt(0)?.tag.toString())
                } else {
                    context.toast("Unable to load stickers")
                }
            }
        }
    }

    private fun loadStickerPack(packName: String) {
        val stickers = ArrayList<String>()
        for (i in 1..20) {
            stickers.add("${RetrofitClient.BASE_URL}img/stickers-png/${packName}/${i}.png")
        }
        binding.stickersPickerRecycler.layoutManager = GridLayoutManager(context, 4)
        binding.stickersPickerRecycler.adapter = StickerPickerRecyclerAdapter(stickers) { position ->
            onStickerClick("${RetrofitClient.BASE_URL}img/stickers-png/${packName}/${position + 1}.png")
        }
    }

    private fun createTabItemView(imgUri: String): View {
        val imageView = ImageView(context)
        imageView.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        Glide.with(this)
            .load(imgUri)
            .apply(RequestOptions().override(100, 100))
            .thumbnail(0.2F)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
        return imageView
    }

    companion object {
        private lateinit var onStickerClick: (imageUrl: String) -> Unit
        fun newInstance(onStickerClick: (imageUrl: String) -> Unit): StickersPickerDialog {
            this.onStickerClick = onStickerClick
            return StickersPickerDialog()
        }
    }
}