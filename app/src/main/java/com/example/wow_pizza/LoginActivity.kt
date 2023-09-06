package com.example.wow_pizza

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonParser


class LoginActivity : AppCompatActivity() {
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_new)

        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val loginButton = findViewById<Button>(R.id.button_login)
        val phoneEditText = findViewById<EditText>(R.id.edit_phone)

        phoneEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (phoneEditText.text.isEmpty()) {
                    phoneEditText.setText("+91")
                }
                phoneEditText.hint = null

            }
        }

        val apiService = ApiService.create()

        loginButton.setOnClickListener {
            val phone = phoneEditText.text.toString()
            if (phone.length < 13) {
                showToast("Invalid Phone Number")
            } else {
                loginButton.visibility = View.GONE
                loadingProgressBar.visibility = View.VISIBLE
                val otpRequest = SendOTP(phone.substring(3))
                val call = apiService.sendOTP(otpRequest)
                Log.i("LoginActivity - send otp", "Phone: $phone")
                call.enqueue(object : Callback<SendOtpResponse> {
                    override fun onResponse(call: Call<SendOtpResponse>, response: Response<SendOtpResponse>) {
                        if (response.isSuccessful) {

                            val otpResponse = response.body()
                            if (otpResponse?.error == null) {
                                storePhone(phone.substring(3))
                                navigateToVerifyOTPActivity()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                val jsonObject = JsonParser.parseString(errorBody).asJsonObject
                                showToast(jsonObject.get("error").asString)
                            }

                            Log.e("LoginActivity - login", "API error: ${response.code()}, Error body: $errorBody")
                        }
                        loadingProgressBar.visibility = View.GONE
                        loginButton.visibility = View.VISIBLE
                    }

                    override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                        loadingProgressBar.visibility = View.GONE
                        loginButton.visibility = View.VISIBLE
                    }
                })
            }

        }

        val token = retrieveToken()
        val phone = retrievePhone()
        if (token != null) {
            navigateToMainActivity()
        } else if (phone != null) {
            navigateToVerifyOTPActivity()
        }

    }

    private fun storeToken(token: String) {
        getSharedPreferences("User", Context.MODE_PRIVATE)
            .edit()
            .putString("token", token)
            .apply()
    }


    private fun retrievePhone(): String? {
        return getSharedPreferences("User", Context.MODE_PRIVATE)
            .getString("phone", null)
    }

    private fun storePhone(phone: String) {
        getSharedPreferences("User", Context.MODE_PRIVATE)
            .edit()
            .putString("phone", phone)
            .apply()
    }

    private fun retrieveToken(): String? {
        return getSharedPreferences("User", Context.MODE_PRIVATE)
            .getString("token", null)
    }

    private fun navigateToVerifyOTPActivity() {
        try {
            val intent = Intent(this, OtpActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
}
