package com.example.wow_pizza

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtpActivity : AppCompatActivity() {
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

//        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val loginButton = findViewById<Button>(R.id.button_login)
        val otpEditText = findViewById<EditText>(R.id.edit_otp)

        val apiService = ApiService.create()

        loginButton.setOnClickListener {
            val otp = otpEditText.text.toString()
            val phone = retrievePhone()
            if (phone != null) {
                val otpRequest = LoginRequest(phone, otp)
                val call = apiService.login(otpRequest)
                Log.i("OtpActivity - login", "Phone: $phone, OTP: $otp")

//            loadingProgressBar.visibility = View.VISIBLE
                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {

                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                val token = loginResponse.access_token
                                storeToken(token)
                                navigateToMainActivity()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val jsonObject = JsonParser.parseString(errorBody).asJsonObject
                            showToast(jsonObject.get("error").asString)
                            Log.e("OtpActivity - login", "API error: ${response.code()}, Error body: $errorBody")
                        }
//                    loadingProgressBar.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                    loadingProgressBar.visibility = View.GONE
                    }
                })
            }

        }

    }

    private fun storeToken(token: String) {
        getSharedPreferences("UserToken", Context.MODE_PRIVATE)
            .edit()
            .putString("token", token)
            .apply()
    }

    private fun retrievePhone(): String? {
        return getSharedPreferences("UserPhone", Context.MODE_PRIVATE)
            .getString("phone", null)
    }

    private fun navigateToMainActivity() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
