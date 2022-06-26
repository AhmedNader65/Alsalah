package com.crazyidea.alsalah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.databinding.ItemFehresBinding
import com.crazyidea.alsalah.databinding.ItemFehresHeaderBinding
import com.crazyidea.alsalah.utils.getJuzName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class SurahAdapter(
    val clickListener: SurahClickListener
) :
    ListAdapter<DataItem,
            RecyclerView.ViewHolder>(SurahDiffCallback()) {


    fun addHeaderAndSubmitList(list: List<DataItem>) {
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val surahItem = getItem(position) as DataItem.surahItem
                holder.bind(clickListener, surahItem.surah)
            }
            is TextViewHolder -> {
                val headerItem = getItem(position) as DataItem.Header
                holder.bind(clickListener, headerItem.id.toString())
            }
        }
    }

    class TextViewHolder(val binding: ItemFehresHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFehresHeaderBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(binding)
            }
        }

        fun bind(clickListener: SurahClickListener, item: String) {
            binding.juzSt = item.getJuzName(context = binding.juz.context )
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.surahItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun getData(): MutableList<DataItem> {
        return currentList
    }

    class ViewHolder private constructor(val binding: ItemFehresBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: SurahClickListener, item: Surah) {
            binding.surah = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFehresBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}

class SurahClickListener(val clickListener: (surah: Surah) -> Unit) {
    fun onClick(surah: Surah) = clickListener(surah)
}

class SurahDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {
    data class surahItem(val surah: Surah) : DataItem() {
        override val id: Long = surah.id!!
    }

    data class Header(val name: String) : DataItem() {
        override val id = name.toLong()
    }

    abstract val id: Long
}