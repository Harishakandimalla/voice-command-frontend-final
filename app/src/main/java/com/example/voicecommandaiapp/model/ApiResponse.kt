package com.example.voicecommandaiapp.model

import com.google.gson.annotations.SerializedName

/**
 * A generic response for simple success/failure messages from the API.
 * Corresponds to backend responses from update, delete, complete, etc.
 */
data class SimpleResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("message", alternate = ["reply"]) val message: String? = null,
    @SerializedName("error") val error: String? = null
)

/**
 * A specific response for the create_task.php endpoint.
 */
data class CreateTaskResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("task_id") val taskId: Int?,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)
