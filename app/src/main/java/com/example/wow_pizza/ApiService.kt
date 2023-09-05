package com.example.wow_pizza

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)

data class CartItem(
    val _id: String,
    var count: Int,
    val img_url: String,
    val item_name: String,
    val price: Double,
    val size: String
)

data class MenuItemsSize(
    val price: Double,
    val size: String
)

data class MenuItems(
    val _id: String,
    val available_sizes: List<MenuItemsSize>,
    val created_at: String,
    val img_url: String,
    val item_group: String,
    val item_name: String,
    val price: Double
)



interface ApiService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("cart-data")
    fun getCartData(@Header("Authorization") authorizationHeader: String): Call<List<CartItem>>

    @GET("items")
    fun getMenuItems(@Header("Authorization") authorizationHeader: String): Call<List<MenuItems>>
}
