package com.example.wow_pizza.data.retrofit

import com.example.wow_pizza.data.model.LoginRequest
import com.example.wow_pizza.data.model.NotificationTokenUpdate
import com.example.wow_pizza.data.model.UpdateUserRequest
import retrofit2.Retrofit

class UserRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun login(loginRequest: LoginRequest) = services.login(loginRequest)
    suspend fun updateUser(updateUserRequest: UpdateUserRequest) = services.updateUser(updateUserRequest)
    suspend fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) = services.updateFcmToken(notificationTokenUpdate)
}