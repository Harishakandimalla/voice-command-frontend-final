package com.example.voicecommandaiapp.model

// As per your exact guide for a stable login
data class LoginResponse(
    val ok: Boolean,
    val message: String?,
    val error: String?,
    val user: User?
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)

