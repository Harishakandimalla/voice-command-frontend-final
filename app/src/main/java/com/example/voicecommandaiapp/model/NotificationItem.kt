package com.example.voicecommandaiapp.model

data class NotificationItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val time: String,
    val isUnread: Boolean,
    val iconRes: Int,
    val backgroundRes: Int,
    val type: String
)
