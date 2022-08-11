package com.crazyidea.alsalah.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.themeColor


class LanguagesAdapter(
    private val dataSet: ArrayList<SupportedLanguage>,
    private val listner: LanguagListner
) :
    RecyclerView.Adapter<LanguagesAdapter.ViewHolder>() {
    private lateinit var context: Context


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val language_status: ImageView
        val language_status_checked: ImageView
        val language_title: TextView

        init {
            // Define click listener for the ViewHolder's View.
            language_status = view.findViewById(R.id.language_status)
            language_status_checked = view.findViewById(R.id.language_status_checked)
            language_title = view.findViewById(R.id.language_title)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        context = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_language, viewGroup, false)

        return ViewHolder(view)


    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val language = dataSet.get(position)


        viewHolder.language_title.text = language.Name
        if (!language.checked) {
            viewHolder.language_status_checked.visibility = View.GONE
            viewHolder.language_status.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.light_grey
                )
            )
        } else {
            viewHolder.language_status_checked.visibility = View.VISIBLE
            viewHolder.language_status.setColorFilter(
                context.themeColor(android.R.attr.colorPrimary)
            )
        }




        viewHolder.itemView.setOnClickListener {
            for (lang in dataSet) {
                lang.checked = false
            }
            language.checked = true
            notifyDataSetChanged()
            listner.onlangPicked(language)
        }

    }


    public interface LanguagListner {
        fun onlangPicked(language: SupportedLanguage)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}