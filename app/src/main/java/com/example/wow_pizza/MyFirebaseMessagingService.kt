package com.example.wow_pizza

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"
const val channelName = "com.example.fcmpushnotification"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        storeDeviceToken(token)
        Log.d("FMS ======> token", "$token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data["title"]?.let {
            remoteMessage.data["body"]?.let { it1 ->
                generateNotification(
                    it,
                    it1
                )
            }
        }
//        if (remoteMessage.getNotification() != null) {
//            Log.d("FMS ======> ", "$token")
//            remoteMessage.notification!!.title!!.let {
//                remoteMessage.notification!!.body?.let { it1 ->
//                generateNotification(
//                    it,
//                    it1
//                )
//                }
//            }
//        }

    }

    private fun storeDeviceToken(deviceToken: String) {
        getSharedPreferences("User", Context.MODE_PRIVATE)
            .edit()
            .putString("deviceToken", deviceToken)
            .apply()
    }


    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.wow_pizza", R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.wow_pizza_logo)

        return remoteView
    }


    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP))

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.wow_pizza_logo)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())

    }
}

