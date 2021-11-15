package ru.maxim.barybians.ui.fragment.stickerPicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_stickers_picker.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.ChatService
import ru.maxim.barybians.utils.toast


class StickersPickerDialog : BottomSheetDialogFragment() {
    private val chatService: ChatService by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        layoutInflater.inflate(R.layout.fragment_stickers_picker, container, false)

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
                        val imageUrl = "${RetrofitClient.BASE_URL}img/stickers-png/${pack.pack}/${pack.icon}"
                        val tab = stickersPickerTabLayout.newTab().setCustomView(createTabItemView(imageUrl))
                        tab.tag = pack.pack
                        stickersPickerTabLayout.addTab(tab)
                    }
                    stickersPickerTabLayout.tabGravity = TabLayout.GRAVITY_FILL
                    stickersPickerTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab) {
                            loadStickerPack(tab.tag.toString())
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab?) {}
                        override fun onTabReselected(tab: TabLayout.Tab?) {}
                    })
                    loadStickerPack(stickersPickerTabLayout.getTabAt(0)?.tag.toString())
                }   else { context?.toast("Unable to load stickers") }
            }
        }
    }

    private fun loadStickerPack(packName: String) {
        val stickers = ArrayList<String>()
        for (i in 1..20) { stickers.add("${RetrofitClient.BASE_URL}img/stickers-png/${packName}/${i}.png") }
        stickersPickerRecycler.layoutManager = GridLayoutManager(context, 4)
        stickersPickerRecycler.adapter = StickerPickerRecyclerAdapter(stickers) { position ->
            onStickerClick("${RetrofitClient.BASE_URL}img/stickers-png/${packName}/${position+1}.png")
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