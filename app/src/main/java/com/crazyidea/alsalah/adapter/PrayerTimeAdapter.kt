package com.crazyidea.alsalah.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.PrayerTimeRefrence
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.utils.GlobalPreferences

class PrayerTimeAdapter(
    private val dataSet: ArrayList<PrayerTimeRefrence>,
    private val listner: PrayerTimeListner
) :
    RecyclerView.Adapter<PrayerTimeAdapter.ViewHolder>() {
    private lateinit var context: Context


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val language_status: ImageView
        val language_title: TextView

        init {
            // Define click listener for the ViewHolder's View.
            language_status = view.findViewById(R.id.language_status)
            language_title = view.findViewById(R.id.language_title)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        context = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_prayer_way, viewGroup, false)

        return ViewHolder(view)


    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val prayerTimeRefrence = dataSet.get(position)


        viewHolder.language_title.text = prayerTimeRefrence.Name
        if (prayerTimeRefrence.checked) {
            viewHolder.language_status.setImageDrawable(context.resources.getDrawable(R.drawable.ic_checked_lang))
        } else {
            viewHolder.language_status.setImageDrawable(context.resources.getDrawable(R.drawable.ic_lang_unchecked))
        }




        viewHolder.itemView.setOnClickListener {
            for (prayer in dataSet) {
                prayer.checked = false
            }
            prayerTimeRefrence.checked = true
            notifyDataSetChanged()
            listner.onPrayerTimePicked(prayerTimeRefrence)
        }

    }


    public interface PrayerTimeListner {
        fun onPrayerTimePicked(prayerTimeRefrence: PrayerTimeRefrence)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}