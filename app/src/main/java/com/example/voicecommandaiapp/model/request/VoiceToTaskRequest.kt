package com.example.voicecommandaiapp.model.request

data class VoiceToTaskRequest(
    val user_id: Int,
    val voice_command: String
)
