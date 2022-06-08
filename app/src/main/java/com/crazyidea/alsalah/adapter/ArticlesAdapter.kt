package com.crazyidea.alsalah.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.Articles


class ArticlesAdapter(
    private val dataSet: ArrayList<Articles>,
    private val listner: ArticleListner
) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {
    private lateinit var context: Context


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blog_image: ImageView
        val likes_img: ImageView
        val blog_title: TextView
        val views_counter: TextView
        val blog_desc: TextView
        val likes_count: TextView
        val comments_count: TextView
        val share_count: TextView

        init {
            // Define click listener for the ViewHolder's View.
            blog_title = view.findViewById(R.id.blog_title)
            blog_image = view.findViewById(R.id.blog_image)
            likes_img = view.findViewById(R.id.likes_img)
            blog_desc = view.findViewById(R.id.blog_desc)
            views_counter = view.findViewById(R.id.views_counter)
            likes_count = view.findViewById(R.id.likes_count)
            comments_count = view.findViewById(R.id.comments_count)
            share_count = view.findViewById(R.id.share_count)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        context = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_blog, viewGroup, false)


        return ViewHolder(view)


    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val article = dataSet.get(position)
        val desc =
            (Html.fromHtml("<br><p>${article.text}</p>", Html.FROM_HTML_MODE_COMPACT))
        viewHolder.blog_desc.setText(desc)
        viewHolder.blog_title.setText(article.title)
        viewHolder.likes_count.setText(article.likes.toString())
        viewHolder.comments_count.setText(article.comments.toString())
        viewHolder.share_count.setText(article.share.toString())
        if (desc.length > 150) {
            viewHolder.blog_desc.text = Html.fromHtml(
                desc.substring(
                    0,
                    150
                ) + "..." + "<font color='blue'> <u>Read More...</u></font>"
            )
        } else {
            viewHolder.blog_desc.text = desc
        }
        viewHolder.blog_desc.setOnClickListener { listner.onArticlePicked(article) }
        if (article.liked)
            viewHolder.likes_img.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_24))
        else
            viewHolder.likes_img.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_border_24))

        viewHolder.likes_img.setOnClickListener {listner.onLikedClicked(article)}


    }


    public interface ArticleListner {
        fun onArticlePicked(article: Articles)
        fun onLikedClicked(article: Articles)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}