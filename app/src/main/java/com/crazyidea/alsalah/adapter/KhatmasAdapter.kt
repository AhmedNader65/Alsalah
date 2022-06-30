package com.crazyidea.alsalah.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.databinding.ItemBookmarkBinding
import com.crazyidea.alsalah.databinding.ItemKhatmaBinding

class KhatmasAdapter(
    val clickListener: KhatmaClickListener
) :
    ListAdapter<Khatma,
            RecyclerView.ViewHolder>(KhatmaDiffCallback()) {


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

    class ViewHolder private constructor(val binding: ItemKhatmaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: KhatmaClickListener, item: Khatma) {
            binding.item = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemKhatmaBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}

class KhatmaClickListener(
    val clickListener: (item: Khatma) -> Unit
) {
    fun onClick(item: Khatma) = clickListener(item)
}

class KhatmaDiffCallback : DiffUtil.ItemCallback<Khatma>() {
    override fun areItemsTheSame(oldItem: Khatma, newItem: Khatma): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Khatma, newItem: Khatma): Boolean {
        return oldItem == newItem
    }
}
