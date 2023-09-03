package com.example.wow_pizza.ui.cart

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
import com.example.wow_pizza.databinding.FragmentCartBinding
import com.example.wow_pizza.ui.cart.CartAdapter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerView

        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl("http://44.211.227.41/wow-api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        lifecycleScope.launch {
            val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTY5MzcxODc0MywianRpIjoiZDZhZDhlNzEtMTA2ZS00YjY1LWEyMTUtYTA5YWYwZTA4NTY3IiwidHlwZSI6ImFjY2VzcyIsInN1YiI6eyJ1c2VyX2lkIjoiNjRlMWY2ZDRkNzE0MGRmZjVkN2VmN2M5Iiwicm9sZSI6ImFkbWluIn0sIm5iZiI6MTY5MzcxODc0MywiZXhwIjoxNjkzODA1MTQzfQ.UKqIlEN1_MaLfMHP4MW_YLxHvDVKB0S5waU8n_9eAyc" // Replace with your actual token
            val call = apiService.getCartData("Bearer $token")

            call.enqueue(object : Callback<List<CartItem>> {
                override fun onResponse(
                    call: Call<List<CartItem>>,
                    response: Response<List<CartItem>>
                ) {
                    if (response.isSuccessful) {
                        val cartItems = response.body()
                        if (cartItems != null) {
                            // Update the UI with the cartItems here
                            val adapter = CartAdapter(cartItems)
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CartFragment - Get cart data", "API error: ${response.code()}, Error body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                    Log.e("CartFragment - Get cart data", "error: $t")
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
