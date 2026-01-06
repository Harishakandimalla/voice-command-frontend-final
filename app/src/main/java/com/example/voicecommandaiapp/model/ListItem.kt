package com.example.voicecommandaiapp.model

sealed class ListItem {

    data class Header(
        val title: String,
        val subtitle: String
    ) : ListItem()

    data class TaskItem(
        val task: Task
    ) : ListItem()
}
