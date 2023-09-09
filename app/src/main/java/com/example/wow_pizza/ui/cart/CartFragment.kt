package com.example.wow_pizza.ui.cart

import android.content.Context
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wow_pizza.ApiService
import com.example.wow_pizza.CartItem
import com.example.wow_pizza.OrderRequest
import com.example.wow_pizza.databinding.FragmentCartNewBinding
import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartFragment : Fragment() {

    private var _binding: FragmentCartNewBinding? = null
    private lateinit var cartAdapter: CartAdapter
    private val binding get() = _binding!!
    private var cartItems: ArrayList<CartItem> = arrayListOf()

    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumOf { it.price * it.count }
        binding.textTotal.text = "Total: ₹$totalPrice"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        _binding = FragmentCartNewBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val apiService = ApiService.create(requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE))

        lifecycleScope.launch {
            val call = apiService.getCartData()

            call.enqueue(object : Callback<ArrayList<CartItem>> {
                override fun onResponse(
                    call: Call<ArrayList<CartItem>>,
                    response: Response<ArrayList<CartItem>>
                ) {
                    if (response.isSuccessful) {
                        cartItems = response.body() ?: arrayListOf()
                        Log.d("CartFragment", "Cart items size: ${cartItems.size}")
                        val recyclerView = binding.recyclerCartItems
                        cartAdapter = CartAdapter(cartItems, apiService, updateTotalPrice = {
                            updateTotalPrice()
                        }, removeItemCallback = {

                            cartAdapter.notifyDataSetChanged()
                            updateTotalPrice()
                        })

                        val layoutManager = LinearLayoutManager(requireContext())
                        recyclerView.layoutManager = layoutManager

                        recyclerView.adapter = cartAdapter
                        cartAdapter.notifyDataSetChanged()
                        updateTotalPrice()


                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CartFragment - Get cart data", "API error: ${response.code()}, Error body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<ArrayList<CartItem>>, t: Throwable) {
                    Log.e("CartFragment - Get cart data", "error: $t")
                }
            })
        }

        binding.placeOrder.setOnClickListener {
            binding.placeOrder.visibility = View.GONE
            binding.loadingProgressBar.visibility = View.VISIBLE
            val orderRequest = OrderRequest(" ", "Delivery", cartItems.sumOf { it.price * it.count })
            apiService.placeOrder(orderRequest)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        binding.placeOrder.visibility = View.VISIBLE
                        binding.loadingProgressBar.visibility = View.GONE
                        cartItems.clear()
                        updateTotalPrice()
                        cartAdapter.notifyDataSetChanged()

                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("HomeFragment", "API call failed: ${t.message}")
                        binding.placeOrder.visibility = View.VISIBLE
                        binding.loadingProgressBar.visibility = View.GONE
                    }

                })
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
