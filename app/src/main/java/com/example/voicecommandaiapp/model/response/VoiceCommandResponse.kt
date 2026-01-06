package com.example.voicecommandaiapp.model.response

import com.google.gson.annotations.SerializedName

data class VoiceCommandResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("action") val action: String?, // "CREATE_TASK" or "CHAT"
    @SerializedName("message") val message: String?,
    @SerializedName("data", alternate = ["task"]) val data: TaskData?,
    @SerializedName("error") val error: String? = null
)

data class TaskData(
    @SerializedName("title") val title: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("time") val time: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("priority") val priority: String?
)
