package com.example.wow_pizza

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val signupButton = findViewById<Button>(R.id.signup_button)
        val phoneEditText = findViewById<EditText>(R.id.edit_phone)
        val firstNameEditText = findViewById<EditText>(R.id.edit_first_name)
        val lastNameEditText = findViewById<EditText>(R.id.edit_last_name)
        val emailEditText = findViewById<EditText>(R.id.edit_email)
        val passwordEditText = findViewById<EditText>(R.id.edit_password)
        val loginLink = findViewById<TextView>(R.id.login_link)

        phoneEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (phoneEditText.text.isEmpty()) {
                    phoneEditText.setText("+91")
                }
                phoneEditText.hint = null

            }
        }

        val apiService = ApiService.create()

        signupButton.setOnClickListener {
            val phone = phoneEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (phone.length < 13) {
                showToast("Invalid Phone Number")
            } else {
                signupButton.visibility = View.GONE
                loadingProgressBar.visibility = View.VISIBLE
                val otpRequest = SendOTP(phone.substring(3))
                val signupRequest = SignupRequest(phone, password, email, firstName, lastName)
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
                        signupButton.visibility = View.VISIBLE
                    }

                    override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                        loadingProgressBar.visibility = View.GONE
                        signupButton.visibility = View.VISIBLE
                    }
                })
            }

        }

        loginLink.setOnClickListener {
            navigateToLoginActivity()
        }

    }


    private fun storePhone(phone: String) {
        getSharedPreferences("User", Context.MODE_PRIVATE)
            .edit()
            .putString("phone", phone)
            .apply()
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


    private fun navigateToLoginActivity() {
        try {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}