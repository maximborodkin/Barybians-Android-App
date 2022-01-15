package ru.maxim.barybians.ui.fragment.stickerPicker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.bumptech.glide.Glide
import ru.maxim.barybians.ui.fragment.stickerPicker.StickerPickerRecyclerAdapter.StickerViewHolder

class StickerPickerRecyclerAdapter(
    private val stickers: ArrayList<String>,
    private val onStickerClick: (position: Int) -> Unit
) : RecyclerView.Adapter<StickerViewHolder>() {

    class StickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val imageView = ImageView(parent.context)
        val imageSize = parent.width / 4
        imageView.layoutParams = LayoutParams(imageSize, imageSize)
        imageView.setPadding(imageSize / 20)
        return StickerViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        val context = holder.itemView.context
        Glide.with(context).load(stickers[position]).into(holder.image)
        holder.image.setOnClickListener { onStickerClick(position) }
    }

    override fun getItemCount(): Int = stickers.size
}