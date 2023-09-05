package com.example.wow_pizza.data.retrofit

import retrofit2.Retrofit

class ShopRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getShops(placeId: String) = services.getShops(placeId)
}