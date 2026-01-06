package com.example.voicecommandaiapp.data.model

import com.google.gson.annotations.SerializedName

data class AiSuggestion(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("suggestion_text")
    val suggestionText: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("is_read")
    val isRead: Int
)
