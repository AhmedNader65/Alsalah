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
import com.squareup.picasso.Picasso


class ArticlesAdapter(
    private val dataSet: ArrayList<Articles>,
    val onReadMore: (Articles) -> Unit,
    val onShare: (Articles) -> Unit,
    val onFavourite: (Articles) -> Unit,
    val isLoggedIn: Boolean = false
) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {
    private lateinit var context: Context


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blogImage: ImageView
        val likesImg: ImageView
        val blogTitle: TextView
        val viewsCounter: TextView
        val blogDesc: TextView
        val likesCount: TextView
        val commentsCount: TextView
        val shareCount: TextView
        val shareImg: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            blogTitle = view.findViewById(R.id.blog_title)
            blogImage = view.findViewById(R.id.blog_image)
            likesImg = view.findViewById(R.id.likes_img)
            shareImg = view.findViewById(R.id.share_img)
            blogDesc = view.findViewById(R.id.blog_desc)
            viewsCounter = view.findViewById(R.id.views_counter)
            likesCount = view.findViewById(R.id.likes_count)
            commentsCount = view.findViewById(R.id.comments_count)
            shareCount = view.findViewById(R.id.share_count)
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
        val article = dataSet[position]
        val desc =
            (Html.fromHtml("<br><p>${article.text}</p>", Html.FROM_HTML_MODE_COMPACT))
        viewHolder.blogDesc.text = desc
        viewHolder.blogTitle.text = article.title
        viewHolder.likesCount.text = article.likes.toString()
        viewHolder.commentsCount.text = article.comments.toString()
        viewHolder.shareCount.text = article.share.toString()
        viewHolder.blogDesc.text = desc
        viewHolder.viewsCounter.text = context.getString(R.string.views_count, article.views)
        Picasso.get().load(article.image).into(viewHolder.blogImage)
        viewHolder.itemView.setOnClickListener { onReadMore(article) }
        if (article.liked)
            viewHolder.likesImg.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_24))
        else
            viewHolder.likesImg.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_border_24))

        viewHolder.shareImg.setOnClickListener {
            onShare(article)
        }
        viewHolder.likesImg.setOnClickListener {
            if (isLoggedIn) {
                article.liked = !article.liked
                if (article.liked) {
                    article.likes = article.likes.plus(1)
                } else {
                    article.likes = article.likes.minus(1)
                }
            }
            onFavourite(article)
            notifyItemChanged(position)
        }


    }


    public interface ArticleListner {
        fun onArticlePicked(article: Articles)
        fun onLikedClicked(article: Articles)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
    fun submitList(it: java.util.ArrayList<Articles>?) {
        dataSet.clear()
        dataSet.addAll(it!!)
        notifyDataSetChanged()
    }


}