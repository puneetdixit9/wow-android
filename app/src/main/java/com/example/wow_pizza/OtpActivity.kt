package com.example.wow_pizza

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtpActivity : AppCompatActivity() {
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var resendOtpProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        resendOtpProgressBar = findViewById(R.id.resendOtpProgressBar)

        val loginButton = findViewById<Button>(R.id.button_login)
        val closeButton = findViewById<ImageButton>(R.id.button_close)
        val otpEditText = findViewById<EditText>(R.id.edit_otp)
        val resendOtpButton = findViewById<TextView>(R.id.text_resend_otp)

        val apiService = ApiService.create()

        loginButton.setOnClickListener {
            val otp = otpEditText.text.toString()
            if (otp.length == 6) {
                val phone = retrievePhone()
                if (phone != null) {
                    val loginRequest = LoginRequest(phone, otp)
                    val call = apiService.login(loginRequest)
                    Log.i("OtpActivity - login", "Phone: $phone, OTP: $otp")

                    loadingProgressBar.visibility = View.VISIBLE
                    loginButton.visibility = View.GONE
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
                            loadingProgressBar.visibility = View.GONE
                            loginButton.visibility = View.VISIBLE
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            loadingProgressBar.visibility = View.GONE
                            loginButton.visibility = View.VISIBLE
                        }
                    })
                }
            } else {
                showToast("Please enter complete OTP")
            }
        }

        closeButton.setOnClickListener {
            clearUserData()
            navigateToLogin()
        }
        resendOtpButton.setOnClickListener {
            val phone = retrievePhone()
            if (phone != null) {
                resendOtpButton.visibility = View.GONE
                resendOtpProgressBar.visibility = View.VISIBLE
                val otpRequest = SendOTP(phone)
                val call = apiService.sendOTP(otpRequest)
                Log.i("OtpActivity - send otp", "Phone: $phone")
                call.enqueue(object : Callback<SendOtpResponse> {
                    override fun onResponse(call: Call<SendOtpResponse>, response: Response<SendOtpResponse>) {
                        if (response.isSuccessful) {
                            val otpResponse = response.body()
                            if (otpResponse?.error == null) {
                                showToast("OTP Resent")
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("OtpActivity - login", "API error: ${response.code()}, Error body: $errorBody")
                        }
                        resendOtpProgressBar.visibility = View.GONE
                        resendOtpButton.visibility = View.VISIBLE
                    }

                    override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                        resendOtpProgressBar.visibility = View.GONE
                        resendOtpButton.visibility = View.VISIBLE
                    }
                })
            }

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

    private fun navigateToMainActivity() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun clearUserData() {
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.remove("phone")
        editor.apply()
    }
}
