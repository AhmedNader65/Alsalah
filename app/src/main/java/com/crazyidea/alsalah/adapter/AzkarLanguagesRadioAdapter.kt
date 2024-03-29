package com.crazyidea.alsalah.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.SupportedLanguage


class AzkarLanguagesRadioAdapter(
    private val dataSet: ArrayList<SupportedLanguage>,
    private val listner: LanguagListner
) :
    RecyclerView.Adapter<AzkarLanguagesRadioAdapter.ViewHolder>() {
    private var selectedLanguage: String = "ar"
    private lateinit var context: Context

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioBtn: RadioButton

        init {
            // Define click listener for the ViewHolder's View.
            radioBtn = view.findViewById(R.id.radioBtn)

        }
    }

    fun updateSelectedLanguage(lang: String) {
        if (lang != selectedLanguage) {
            selectedLanguage = lang
            notifyDataSetChanged()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        context = viewGroup.context
        findMyLanguage()
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_radio_btn, viewGroup, false)

        return ViewHolder(view)


    }

    private fun findMyLanguage() {
        dataSet.find { it.shortcut == this.selectedLanguage }?.checked = true

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val language = dataSet.get(position)


        viewHolder.radioBtn.text = language.Name



        viewHolder.radioBtn.isChecked = language.checked

        viewHolder.itemView.setOnClickListener {
            for (lang in dataSet) {
                lang.checked = false
            }
            language.checked = true
            notifyDataSetChanged()
            listner.onlangPicked(language)
        }

    }


    interface LanguagListner {
        fun onlangPicked(language: SupportedLanguage)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}