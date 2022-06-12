package com.crazyidea.alsalah.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.Comment
import com.crazyidea.alsalah.utils.GlobalPreferences

class RepliesAdapter(
    private var spots: ArrayList<Comment>
) : RecyclerView.Adapter<RepliesAdapter.ViewHolder>() {
    lateinit var globalPreferences: GlobalPreferences
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return ViewHolder(inflater.inflate(R.layout.item_reply, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        globalPreferences = GlobalPreferences(context)
//        holder.username.text = spot.owner
        holder.datetime.text = spot.created_at
        holder.review.text = spot.comment
//        Picasso.get().load(spot.avatar).into(holder.store_img)


    }

    override fun getItemCount(): Int {
        return spots.size
    }

    fun addComment(comment: Comment) {
        spots.add(comment)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username)
        var datetime: TextView = view.findViewById(R.id.datetime)
        var review: TextView = view.findViewById(R.id.review)
        var store_img: ImageView = view.findViewById(R.id.store_img)
        var root: ConstraintLayout = view.findViewById(R.id.root)
    }


}