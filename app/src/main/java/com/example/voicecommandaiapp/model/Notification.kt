package com.example.voicecommandaiapp.model

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)
