package com.example.wow_pizza.ui.home

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
import androidx.recyclerview.widget.RecyclerView
import com.example.wow_pizza.ApiService
import com.example.wow_pizza.CartItem
import com.example.wow_pizza.MenuItems
import com.example.wow_pizza.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class AddedCartItem(val menuItem: MenuItems, var count: Int) {
}


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var menuItems: List<MenuItems> = emptyList()
    private var cartItems: MutableList<AddedCartItem> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var apiService = ApiService.create(requireActivity().getSharedPreferences("UserToken", Context.MODE_PRIVATE))


        lifecycleScope.launch {

            val cartDataAPI = apiService.getCartData()
            val menuItemApi = apiService.getMenuItems()

            cartDataAPI.enqueue(object : Callback<List<CartItem>> {
                override fun onResponse(
                    call: Call<List<CartItem>>,
                    response: Response<List<CartItem>>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body() ?: emptyList()
                        responseData.forEach { cartItem ->
                            cartItems.add(AddedCartItem(
                                MenuItems(
                                    _id =cartItem._id,
                                    img_url =cartItem.img_url,
                                    item_group ="",
                                    item_name = cartItem.item_name,
                                    price =cartItem.price
                                ), cartItem.count))
                        }

                        Log.d("CartFragment", "Cart items size: ${cartItems.size}")


                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CartFragment - Get cart data", "API error: ${response.code()}, Error body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                    Log.e("CartFragment - Get cart data", "error: $t")
                }
            })

            menuItemApi.enqueue(object : Callback<List<MenuItems>> {
                override fun onResponse(
                    call: Call<List<MenuItems>>,
                    response: Response<List<MenuItems>>
                ) {
                    if (response.isSuccessful) {
                        menuItems = response.body() ?: emptyList()
                        recyclerView = binding.recyclerFoodItems
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        menuAdapter = MenuAdapter(menuItems, cartItems, apiService)
                        recyclerView.adapter = menuAdapter

                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("HomeFragment - Get menu data", "API error: ${response.code()}, Error body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<List<MenuItems>>, t: Throwable) {
                    Log.e("HomeFragment - Get menu data", "error: $t")
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