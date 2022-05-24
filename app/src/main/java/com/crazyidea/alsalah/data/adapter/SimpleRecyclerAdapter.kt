package com.crazyidea.apparch.adapter

import androidx.annotation.LayoutRes
import com.crazyidea.alsalah.data.adapter.BaseRecyclerAdapter
import com.crazyidea.alsalah.data.adapter.BaseViewHolder

class SimpleRecyclerAdapter<RecyclerData : Any>(
    data: List<RecyclerData>, @LayoutRes layoutID: Int,
    private val onBindView: BaseViewHolder<RecyclerData>.(data: RecyclerData) -> Unit
) : BaseRecyclerAdapter<RecyclerData>(data) {

    override val layoutItemId: Int = layoutID

    override fun onBindViewHolder(holder: BaseViewHolder<RecyclerData>, position: Int) {
        holder.onBindView(dataList[position])
    }

}