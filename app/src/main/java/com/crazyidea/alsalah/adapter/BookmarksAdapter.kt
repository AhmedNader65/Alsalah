package com.crazyidea.alsalah.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.databinding.ItemBookmarkBinding


private const val ITEM_VIEW_TYPE_PAGE = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class BookmarksAdapter(
    val clickListener: BookmarkClickListener
) :
    ListAdapter<BookmarkItem,
            RecyclerView.ViewHolder>(BookmarkDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(clickListener, getItem(position))
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BookmarkItem.AyaItem -> ITEM_VIEW_TYPE_ITEM
            is BookmarkItem.PageItem -> ITEM_VIEW_TYPE_PAGE
        }
    }

    fun getData(): MutableList<BookmarkItem> {
        return currentList
    }

    class ViewHolder private constructor(val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: BookmarkClickListener, item: BookmarkItem) {
            binding.item = item
            binding.name.text = item.text
            binding.page.text = (item).id.toString()
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBookmarkBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}

class BookmarkClickListener(
    val clickListener: (item: BookmarkItem) -> Unit
) {
    fun onClick(item: BookmarkItem) = clickListener(item)
}

class BookmarkDiffCallback : DiffUtil.ItemCallback<BookmarkItem>() {
    override fun areItemsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
        return oldItem == newItem
    }
}

sealed class BookmarkItem {
    data class AyaItem(val aya: Ayat) : BookmarkItem() {
        override val id: Long = aya.page.toLong()
        override val text: String = aya.text
    }

    data class PageItem(val page: Long, val content: String) : BookmarkItem() {
        override val id = page
        override val text = content
    }

    abstract val id: Long
    abstract val text: String
}