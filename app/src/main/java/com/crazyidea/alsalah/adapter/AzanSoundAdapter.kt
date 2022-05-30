package com.crazyidea.alsalah.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.Azan
import com.crazyidea.alsalah.utils.GlobalPreferences

class AzanSoundAdapter(
    private val dataSet: ArrayList<Azan>,
    private val listner: AzanListner
) :
    RecyclerView.Adapter<AzanSoundAdapter.ViewHolder>() {
    private lateinit var context: Context

    lateinit var globalPreferences: GlobalPreferences

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioBtn: RadioButton
        val play: ImageView
        val line: View

        init {
            // Define click listener for the ViewHolder's View.
            radioBtn = view.findViewById(R.id.radioBtn)
            play = view.findViewById(R.id.play)
            line = view.findViewById(R.id.line)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        context = viewGroup.context
        globalPreferences = GlobalPreferences(context)
        findMyAzan()
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_azan_radio, viewGroup, false)


        return ViewHolder(view)


    }

    private fun findMyAzan() {
        dataSet.find { it.Name == globalPreferences.getAzan() }?.checked = true

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val azan = dataSet.get(position)

        if (!azan.hasline)
            viewHolder.line.visibility = View.GONE
        viewHolder.radioBtn.text = azan.Name

        viewHolder.play.setOnClickListener {
            for (azan2 in dataSet) {
                if (azan2 != azan)
                    azan2.isPlaying = false
            }
            azan.isPlaying = !azan.isPlaying
            notifyDataSetChanged()
            listner.onPlayClicked(azan)
        }
        if (azan.isPlaying)
            viewHolder.play.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_pause_24))
        else
            viewHolder.play.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_play_arrow_24))
        viewHolder.radioBtn.isChecked = azan.checked

        viewHolder.radioBtn.setOnClickListener {
            for (azan in dataSet) {
                azan.checked = false
            }
            azan.checked = true
            notifyDataSetChanged()
            listner.onAzanPicked(azan)
        }

    }


    public interface AzanListner {
        fun onAzanPicked(azan: Azan)
        fun onPlayClicked(azan: Azan)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}