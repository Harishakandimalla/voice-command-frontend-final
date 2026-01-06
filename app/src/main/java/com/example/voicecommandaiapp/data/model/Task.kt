package com.example.voicecommandaiapp.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("task_date")
    val taskDate: String?,

    @SerializedName("task_time")
    val taskTime: String?,

    @SerializedName("category")
    val category: String,

    @SerializedName("priority")
    val priority: String,

    @SerializedName("status")
    val status: String
)
