package com.example.wow_pizza

import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path

data class LoginRequest(val phone: String, val otp: String, val device_token: String)


data class OrderRequest(val order_note: String, val order_type: String, val total: Double)


data class SendOTP(val phone: String)

data class SignupRequest(
    val phone: String,
    val password: String,
    val email: String,
    val first_name: String,
    val last_name: String
)

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
    val img_url: String,
    val item_group: String,
    val item_name: String,
    val price: Double
)

sealed class APIResult<out T> {
    data class Success<out T>(val data: T) : APIResult<T>()
    data class Error(val message: String) : APIResult<Nothing>()
}

interface ApiService {

    @POST("auth/otp")
    fun sendOTP(@Body request: SendOTP): Call<SendOtpResponse>

    @POST("auth/otp")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("cart-data")
    fun getCartData(): Call<ArrayList<CartItem>>

    @GET("items")
    fun getMenuItems(): Call<ArrayList<MenuItems>>

    @POST("order")
    fun placeOrder(@Body request: OrderRequest): Call<Void>

    @POST("add-to-cart/{itemId}/{count}/{size}")
    fun addToCart(
        @Path("itemId") itemId: String,
        @Path("count") count: String,
        @Path("size") size: String
    ): Call<Void>


    companion object {
        private const val BASE_URL = "https://puneetdixit.in/wow-api/"

        fun create(sharedPreferences : SharedPreferences): ApiService {

            val token = sharedPreferences.getString("token", "")
            val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val requestWithToken = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(requestWithToken)
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            return retrofit.create(ApiService::class.java)
        }

        fun create(): ApiService {

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}
