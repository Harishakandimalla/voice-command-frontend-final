package com.example.voicecommandaiapp.model.request

import com.google.gson.annotations.SerializedName

/**
 * Data class for the body of a POST request to tasks/complete_task.php
 */
// Added a comment to force IDE to re-index
data class UpdateTaskStatusRequest(
    @SerializedName("task_id") val taskId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("status") val status: String // Send status as a string, e.g., "COMPLETED"
)
