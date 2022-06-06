package com.crazyidea.alsalah.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr

class FajrAdapter(
    private var contacts: List<Fajr>,
//    private val listener: OnProductInteract,
    var layout: Int = R.layout.item_textview
) :
    RecyclerView.Adapter<FajrAdapter.ViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }


    fun setData(contactsList: List<Fajr>) {
        this.contacts = contactsList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(contact: Fajr) {
            name.text = contact.name
        }

        val name: TextView = itemView.findViewById(R.id.text1)
    }

//    interface OnProductInteract {
//        fun onAddToCart(productId: Int)
//    }
}