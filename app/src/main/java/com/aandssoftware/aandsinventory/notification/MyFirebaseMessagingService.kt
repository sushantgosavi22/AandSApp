package com.aandssoftware.aandsinventory.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.ui.activity.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        NotificationUtil.onNewToken(token, this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        showNotification(remoteMessage)
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        var title : String?
        var body : String?
        if (remoteMessage.notification != null) {
            title = remoteMessage.notification?.title
            body = remoteMessage.notification?.body
        } else {
            title = remoteMessage.data["title"]
            body = remoteMessage.data["message"]
        }
        val notificationIntent = Intent(this, SplashActivity::class.java)
        remoteMessage.data.forEach { (key, value) -> notificationIntent.putExtra(key,value) }
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.channel_name)
        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannels(channelId, channelName, notificationManager)
        }
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.aands_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)

        val notificationId = (Date().getTime() / 1000L % Int.MAX_VALUE) as Int
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels(channelId: String, channelName: String, notificationManager: NotificationManager) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }


}