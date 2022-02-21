package ru.maxim.barybians.ui.fragment.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.databinding.ItemLoadingStateBinding
import ru.maxim.barybians.ui.fragment.feed.LoadingStateAdapter.LoadingStateViewHolder
import javax.inject.Inject

class LoadingStateAdapter @Inject constructor() : LoadStateAdapter<LoadingStateViewHolder>() {

    private var onRetryClick: (() -> Unit)? = null

    fun setOnRetryListener(listener: (() -> Unit)?) {
        onRetryClick = listener
    }

    class LoadingStateViewHolder(private val binding: ItemLoadingStateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) = with(binding) {
            val context = binding.root.context
            itemLoadingStateProgressBar.isVisible = loadState is LoadState.Loading
            itemLoadingStateErrorMessage.isVisible = loadState is LoadState.Error
            itemLoadingStateRetryButton.isVisible = loadState is LoadState.Error
            if (loadState is LoadState.Error) {
                itemLoadingStateErrorMessage.text = when (loadState.error) {
                    is NoConnectionException -> context.getString(R.string.no_internet_connection)
                    is TimeoutException -> context.getString(R.string.request_timeout)
                    else -> context.getString(R.string.common_network_error)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadingStateBinding.inflate(layoutInflater, parent, false)
        binding.itemLoadingStateRetryButton.setOnClickListener { onRetryClick?.invoke() }
        return LoadingStateViewHolder(binding)
    }
}