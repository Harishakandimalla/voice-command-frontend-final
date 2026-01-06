package com.example.voicecommandaiapp.model.request

import com.google.gson.annotations.SerializedName

/**
 * Data class for the body of a POST request to tasks/delete_task.php
 */
data class DeleteTaskRequest(
    @SerializedName("task_id") val taskId: Int,
    @SerializedName("user_id") val userId: Int
)
