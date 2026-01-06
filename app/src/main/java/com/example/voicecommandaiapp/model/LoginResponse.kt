package com.example.voicecommandaiapp.model

// As per your exact guide for a stable login
data class LoginResponse(
    val ok: Boolean,
    val error: String?,
    val user_id: Int?,
    val user_name: String?,
    val message: String?
)
