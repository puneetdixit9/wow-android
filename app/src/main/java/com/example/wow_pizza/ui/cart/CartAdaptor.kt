package com.example.wow_pizza.ui.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wow_pizza.ApiService
import com.example.wow_pizza.databinding.ItemCartProductBinding
import com.example.wow_pizza.CartItem
import com.example.wow_pizza.MenuItems
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val apiService: ApiService,
    private val updateTotalPrice: () -> Unit,
    private val removeItemCallback: (CartItem) -> Unit
) :
    RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    fun addToCart(menuItem: MenuItems, count: Int, binding: ItemCartProductBinding, cartItem: CartItem) {
        val itemId = menuItem._id
        val size = "regular"

        apiService.addToCart(itemId, count.toString(), size)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    binding.textFoodPrice.text = "₹${cartItem.price * cartItem.count}"
                    binding.layoutQuantityControl.textQuantity.text = cartItem.count.toString()
                    updateTotalPrice()
                    if (cartItem.count == 0) {
                        removeItemCallback(cartItem)
                        binding.layoutRoot.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("HomeFragment", "API call failed: ${t.message}")
                }
            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartProductBinding.inflate(inflater, parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartItemViewHolder(private val binding: ItemCartProductBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.textFoodName.text = cartItem.item_name
            binding.textFoodPrice.text = "₹${cartItem.price * cartItem.count}"
            binding.layoutQuantityControl.subItem.visibility = View.VISIBLE
            binding.layoutQuantityControl.textQuantity.text = cartItem.count.toString()

            binding.layoutQuantityControl.addItem.setOnClickListener {
                val itemId = cartItem._id
                cartItem.count++
                addToCart(
                    MenuItems(
                        _id = cartItem._id,
                        img_url = cartItem.img_url,
                        item_group = "",
                        item_name = cartItem.item_name,
                        price = cartItem.price
                    ), cartItem.count, binding, cartItem
                )
            }

            binding.layoutQuantityControl.subItem.setOnClickListener {
                cartItem.count--
                addToCart(
                    MenuItems(
                        _id = cartItem._id,
                        img_url = cartItem.img_url,
                        item_group = "",
                        item_name = cartItem.item_name,
                        price = cartItem.price
                    ), cartItem.count, binding, cartItem
                )
            }
        }
    }
}
