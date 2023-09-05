package com.example.wow_pizza.data.model
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class NotificationModel(
    var type: String?,
    var title: String?,
    var message: String?,
    var payload: JSONObject
)