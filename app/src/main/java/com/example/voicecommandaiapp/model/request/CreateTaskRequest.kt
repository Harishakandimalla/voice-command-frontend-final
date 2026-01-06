package com.example.voicecommandaiapp.model.request

import com.google.gson.annotations.SerializedName

/**
 * Data class for the body of a POST request to tasks/create_task.php
 */
data class CreateTaskRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("task_date") val taskDate: String,
    @SerializedName("task_time") val taskTime: String?, // Corrected JSON key and made nullable
    @SerializedName("category") val category: String,
    @SerializedName("priority") val priority: String // Priority as a String (e.g., "HIGH")
)
