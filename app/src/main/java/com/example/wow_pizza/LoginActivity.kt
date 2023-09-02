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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://44.211.227.41/wow-api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val loginRequest = LoginRequest(email, password)
            val call = apiService.login(loginRequest)
            Log.i("LoginActivity - login", "Email: $email and Password: $password")

            loadingProgressBar.visibility = View.VISIBLE
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
                        Log.e("LoginActivity - login", "API error: ${response.code()}, Error body: $errorBody")
                    }
                    loadingProgressBar.visibility = View.GONE
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loadingProgressBar.visibility = View.GONE
                }
            })
        }

        val token = retrieveToken()
        if (token != null) {
            navigateToMainActivity()
        }
    }

    private fun storeToken(token: String) {
        getSharedPreferences("UserToken", Context.MODE_PRIVATE)
            .edit()
            .putString("token", token)
            .apply()
    }

    private fun retrieveToken(): String? {
        return getSharedPreferences("UserToken", Context.MODE_PRIVATE)
            .getString("token", null)
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
