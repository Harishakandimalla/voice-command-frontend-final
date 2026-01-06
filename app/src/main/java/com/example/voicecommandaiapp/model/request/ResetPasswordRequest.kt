package com.example.voicecommandaiapp.model.request

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("new_password") val password: String
)
