package com.example.voicecommandaiapp.model

data class TaskCountResponse(
    val ok: Boolean,
    val counts: TaskCounts? = null,
    val error: String? = null
)
