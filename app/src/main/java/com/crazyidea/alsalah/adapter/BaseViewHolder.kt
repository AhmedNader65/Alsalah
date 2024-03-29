package com.crazyidea.alsalah.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.utils.inflater

class BaseViewHolder<in T>(parent: ViewGroup, @LayoutRes layoutId: Int) :
    RecyclerView.ViewHolder(parent.inflater(layoutId)), LayoutContainer {

    override val containerView: View
        get() = itemView

}

interface LayoutContainer {
    /** Returns the root holder view. */
    val containerView: View?
}