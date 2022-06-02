package com.crazyidea.hayah.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crazyidea.hayah.R
import com.crazyidea.hayah.model.Product

class ProductsAdapter(
    private var products: ArrayList<Product>,
    private val listener: OnProductInteract,
    var layout: Int = R.layout.item_list_product
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = products[position]
        Glide.with(context).load(product.image).into(holder.img)
        holder.name.text = product.name
        holder.category.text = product.category
        holder.desc.text = product.description
        holder.productPrice.text = "${product.final_price} ${context.getString(R.string.currency)}"
        if (product.discount != "0") {
            holder.oldPrice.visibility = VISIBLE
            holder.discountLine.visibility = VISIBLE
            holder.offerTV.visibility = VISIBLE
            holder.offerTV.text = context.getString(R.string.discount_percent, product.discount)
            holder.oldPrice.text = "${product.price} ${context.getString(R.string.currency)}"
        }else{
            holder.oldPrice.visibility = GONE
            holder.discountLine.visibility = GONE
            holder.offerTV.visibility = GONE
        }
        holder.addToCartBtn.setOnClickListener { _ ->
            listener.onAddToCart(product.id)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }


    fun setData(homeProducts: java.util.ArrayList<Product>) {
        this.products = homeProducts
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.productImg)
        val name: TextView = itemView.findViewById(R.id.productName)
        val category: TextView = itemView.findViewById(R.id.category)
        val desc: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val addToCartBtn: Button = itemView.findViewById(R.id.addToCartBtn)
        val oldPrice: TextView = itemView.findViewById(R.id.productOldPrice)
        val discountLine: View = itemView.findViewById(R.id.discountLine)
        val offerTV: TextView = itemView.findViewById(R.id.offerTV)
    }

    interface OnProductInteract {
        fun onAddToCart(productId: Int)
    }
}