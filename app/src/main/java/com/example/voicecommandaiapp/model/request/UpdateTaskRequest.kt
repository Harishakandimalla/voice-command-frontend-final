package com.example.voicecommandaiapp.model.request

import com.google.gson.annotations.SerializedName

/**
 * Data class for the body of a POST request to tasks/update_task.php
 */
data class UpdateTaskRequest(
    @SerializedName("task_id") val taskId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("task_date") val taskDate: String, // Corrected key
    @SerializedName("task_time") val taskTime: String?, // Corrected key and made nullable
    @SerializedName("category") val category: String,
    @SerializedName("priority") val priority: String
)
