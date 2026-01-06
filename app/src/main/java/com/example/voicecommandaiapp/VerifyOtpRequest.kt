package com.example.voicecommandaiapp.network

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)
