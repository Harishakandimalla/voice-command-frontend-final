package com.example.voicecommandaiapp.model

import com.example.voicecommandaiapp.model.Task
import com.google.gson.annotations.SerializedName

data class VoiceTaskResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("task") val task: Task?,
    @SerializedName("message") val message: String?
)
