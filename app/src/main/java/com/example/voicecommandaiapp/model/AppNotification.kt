package com.example.voicecommandaiapp.model

data class AppNotification(
    val id: Int,
    val user_id: Int?,
    val message: String?,
    val is_read: Boolean?,
    val created_at: String?
)
