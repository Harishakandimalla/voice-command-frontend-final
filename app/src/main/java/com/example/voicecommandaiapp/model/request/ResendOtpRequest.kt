package com.example.voicecommandaiapp.model.request

import com.google.gson.annotations.SerializedName

data class ResendOtpRequest(
    @SerializedName("email") val email: String
)
