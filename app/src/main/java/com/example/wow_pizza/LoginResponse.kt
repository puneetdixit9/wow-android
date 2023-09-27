package com.example.wow_pizza

data class LoginResponse(
    val access_token: String,
    val expires: Double,
    val refresh_token: String,
    val role: String,
    val token_type: String
)


data class SendOtpResponse(
    val status: String,
    val error: String
)

data class SignupResponse(
    val status: String,
    val error: String
)
