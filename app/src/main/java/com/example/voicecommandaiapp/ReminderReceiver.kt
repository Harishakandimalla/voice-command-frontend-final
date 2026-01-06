package com.example.voicecommandaiapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val title = intent.getStringExtra("TASK_TITLE") ?: "Task Reminder"
        
        if (taskId != -1) {
            NotificationHelper.showNotification(context, title, "You have a task due now!")
        }
    }
}
