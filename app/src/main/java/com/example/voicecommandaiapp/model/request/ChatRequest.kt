package com.example.voicecommandaiapp.model.request

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("text") val message: String
)
