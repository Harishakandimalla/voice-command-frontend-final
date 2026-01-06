package com.example.voicecommandaiapp.model

import java.io.Serializable

data class Task(
    val id: Int,
    val title: String?,
    val description: String?,
    val task_date: String?,
    val task_time: String?,
    val category: String?,
    val priority: String?,
    val status: String?,
    val reminder: String?,
    val repeat: String?
) : Serializable
