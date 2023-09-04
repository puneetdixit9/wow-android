package com.example.wow_pizza.ui.cart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.wow_pizza.ApiService
import com.example.wow_pizza.CartItem
import com.example.wow_pizza.R
import com.example.wow_pizza.databinding.FragmentCartBinding
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
    private var cartItems: List<CartItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val sharedPreferences = requireActivity().getSharedPreferences("UserToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl("http://44.211.227.41/wow-api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val columnLayoutParams = TableRow.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.cart_other_width),
            TableRow.LayoutParams.WRAP_CONTENT,
            1F
        )

// Create a layout parameter for the "Item" column with a fixed width
        val fixedWidthLayoutParams = TableRow.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.cart_item_width), // Replace with the correct dimension resource
            TableRow.LayoutParams.WRAP_CONTENT,
            1F
        )

        val itemSizeLayoutParams = TableRow.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.cart_size_width), // Replace with the correct dimension resource
            TableRow.LayoutParams.WRAP_CONTENT,
            1F
        )

        lifecycleScope.launch {
            val call = apiService.getCartData("Bearer $token")

            call.enqueue(object : Callback<List<CartItem>> {
                override fun onResponse(
                    call: Call<List<CartItem>>,
                    response: Response<List<CartItem>>
                ) {
                    if (response.isSuccessful) {
                        cartItems = response.body() ?: emptyList()
                        val tableLayout = binding.cartTable

                        for (cartItem in cartItems) {
                            val tableRow = TableRow(requireContext())

                            val itemTextView = TextView(requireContext())
                            itemTextView.text = cartItem.item_name
                            itemTextView.gravity = Gravity.START
                            itemTextView.layoutParams = fixedWidthLayoutParams // Use the fixed-width layout parameters for the "Item" column
                            itemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            tableRow.addView(itemTextView)

                            val sizeTextView = TextView(requireContext())
                            sizeTextView.text = cartItem.size
                            sizeTextView.gravity = Gravity.CENTER
                            sizeTextView.layoutParams = itemSizeLayoutParams // Use the common layout parameters for other columns
                            sizeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            tableRow.addView(sizeTextView)

                            val qtyTextView = TextView(requireContext())
                            qtyTextView.text = cartItem.count.toString()
                            qtyTextView.gravity = Gravity.CENTER
                            qtyTextView.layoutParams = columnLayoutParams // Use the common layout parameters for other columns
                            qtyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            tableRow.addView(qtyTextView)

                            val rateQtyTextView = TextView(requireContext())
                            rateQtyTextView.text = "${cartItem.price}"
                            rateQtyTextView.gravity = Gravity.CENTER
                            rateQtyTextView.layoutParams = columnLayoutParams // Use the common layout parameters for other columns
                            rateQtyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            tableRow.addView(rateQtyTextView)

                            val sumTextView = TextView(requireContext())
                            sumTextView.text = "${cartItem.price * cartItem.count}"
                            sumTextView.gravity = Gravity.END
                            sumTextView.layoutParams = columnLayoutParams // Use the common layout parameters for other columns
                            sumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            tableRow.addView(sumTextView)
                            tableLayout.addView(tableRow)

                            val spaceView = View(requireContext())
                            val spaceLayoutParams = TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                resources.getDimensionPixelSize(R.dimen.cart_row_space_height)
                            )
                            spaceView.layoutParams = spaceLayoutParams
                            val tableRowForSpace = TableRow(requireContext())
                            tableRowForSpace.addView(spaceView)

                            tableLayout.addView(tableRowForSpace)

                        }


                        val totalAmountTextView = binding.totalAmount
                        val totalAmount = cartItems.sumOf { it.price * it.count }
                        totalAmountTextView.text = "₹${totalAmount}"
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
