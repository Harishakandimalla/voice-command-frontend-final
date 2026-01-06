package com.example.voicecommandaiapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    private const val CHANNEL_ID = "task_reminder_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminders"
            val descriptionText = "Reminders for upcoming tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, title: String, message: String) {
        val sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val soundName = sharedPreferences.getString("notification_sound", "Default")
        val soundUri = getSoundUri(context, soundName)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(soundUri)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun getSoundUri(context: Context, soundName: String?): Uri? {
        if (soundName == null || soundName == "Default") {
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        val resourceId = context.resources.getIdentifier(soundName.toLowerCase(), "raw", context.packageName)
        return if (resourceId != 0) {
            Uri.parse("android.resource://${context.packageName}/$resourceId")
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }
}
