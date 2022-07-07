package com.crazyidea.alsalah.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import timber.log.Timber

class ContactsAdapter(
    private var contacts: List<Fajr>,
    private var list: List<Fajr>,
    private var removeContact: (contact: Fajr) -> Unit,
    private var addContact: (contact: Fajr) -> Unit,
    var layout: Int = R.layout.item_checkbox
) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.name.setOnCheckedChangeListener(null)
        contact.checked = list.any { it.number == contact.number }
        holder.bind(contact)
        holder.name.setOnCheckedChangeListener { buttonView, isChecked ->
            Timber.e("check changed")
            if (isChecked != contact.checked) {
                if (isChecked) {
                    addContact(contact)
                    contact.checked = true
                } else {
                    removeContact(contact)
                }
            }
        }
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
            name.isChecked = contact.checked
        }

        val name: CheckBox = itemView.findViewById(R.id.checkbox)
    }
}