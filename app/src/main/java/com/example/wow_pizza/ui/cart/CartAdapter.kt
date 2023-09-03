package com.example.wow_pizza.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wow_pizza.CartItem
import com.example.wow_pizza.R

class CartAdapter(private val cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val itemSize: TextView = itemView.findViewById(R.id.item_size)
        val itemCount: TextView = itemView.findViewById(R.id.item_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        Glide.with(holder.itemImage.context)
            .load(cartItem.img_url)
            .placeholder(R.drawable.pizza_image) // You can set a placeholder image
            .into(holder.itemImage)

        holder.itemName.text = cartItem.item_name
        holder.itemPrice.text = "Price: $${cartItem.price}"
        holder.itemSize.text = "Size: ${cartItem.size}"
        holder.itemCount.text = "Count: ${cartItem.count}"
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
