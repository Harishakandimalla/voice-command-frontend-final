package com.example.voicecommandaiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.*

object TaskManager {

    private val _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> = _tasks

    fun setTasks(list: List<Task>) {
        _tasks.value = list
    }

    fun addTask(task: Task) {
        val current = _tasks.value?.toMutableList() ?: mutableListOf()
        current.add(task)
        _tasks.value = current
    }

    fun completeTask(taskId: Int) {
        _tasks.value = _tasks.value?.map {
            if (it.id == taskId) it.copy(status = TaskStatus.COMPLETED.name) else it
        }
    }

    fun todayTasks(): List<Task> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return _tasks.value?.filter { it.task_date == today } ?: emptyList()
    }
}
