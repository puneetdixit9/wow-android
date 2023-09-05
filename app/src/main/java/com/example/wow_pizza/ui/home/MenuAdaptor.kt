package com.example.wow_pizza.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wow_pizza.ApiService
import com.example.wow_pizza.MenuItems
import com.example.wow_pizza.databinding.ItemFoodBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuAdapter(
    private val menuItems: List<MenuItems>,
    private val cartItems: MutableList<AddedCartItem>,
    private val apiService: ApiService
    ) :
    RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodBinding.inflate(inflater, parent, false)
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    fun addToCart(menuItem: MenuItems, count: Int) {
        val itemId = menuItem._id
        val size = "regular"

        apiService.addToCart(itemId, count.toString(), size)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("HomeFragment", "API call failed: ${t.message}")
                }
            })
    }

    inner class MenuItemViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItems) {
            binding.textFoodName.text = menuItem.item_name
            binding.textFoodPrice.text = "₹${menuItem.price}"
            Picasso.get().load(menuItem.img_url).into(binding.imageFood)


            val itemCount = cartItems.find { it.menuItem._id == menuItem._id }?.count ?: 0
            if (itemCount != 0) {
                binding.layoutQuantityControl.subItem.visibility = View.VISIBLE
                binding.layoutQuantityControl.textQuantity.text = itemCount.toString()
            }

            // Add item to cart handling
            binding.layoutQuantityControl.addItem.setOnClickListener {
                val itemId = menuItem._id
                binding.layoutQuantityControl.subItem.visibility = View.VISIBLE


                val cartItem = cartItems.find { it.menuItem._id == itemId }
                if (cartItem != null) {
                    cartItem.count++
                } else {
                    cartItems.add(AddedCartItem(menuItem, 1))
                }
                val itemCount = cartItems.find { it.menuItem._id == itemId }?.count ?: 0
                binding.layoutQuantityControl.textQuantity.text = itemCount.toString()
                addToCart(menuItem, itemCount)
            }

            // Remove item from cart handling
            binding.layoutQuantityControl.subItem.setOnClickListener {
                val itemId = menuItem._id

                val cartItem = cartItems.find { it.menuItem._id == itemId }
                if (cartItem != null) {
                    cartItem.count--
                }
                val itemCount = cartItems.find { it.menuItem._id == itemId }?.count ?: 0
                if (itemCount == 0) {
                    binding.layoutQuantityControl.textQuantity.text = "Add"
                    binding.layoutQuantityControl.subItem.visibility = View.GONE
                } else {
                    binding.layoutQuantityControl.textQuantity.text = itemCount.toString()
                }
                addToCart(menuItem, itemCount)
            }
        }
    }
}

