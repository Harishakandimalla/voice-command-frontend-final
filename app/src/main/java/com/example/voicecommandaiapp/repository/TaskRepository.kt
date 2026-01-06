package com.example.voicecommandaiapp.repository

import com.example.voicecommandaiapp.model.TaskResponse
import com.example.voicecommandaiapp.model.TasksResponse
import com.example.voicecommandaiapp.model.CreateTaskResponse
import com.example.voicecommandaiapp.model.SimpleResponse
import com.example.voicecommandaiapp.model.request.CreateTaskRequest
import com.example.voicecommandaiapp.model.request.DeleteTaskRequest
import com.example.voicecommandaiapp.model.request.UpdateTaskRequest
import com.example.voicecommandaiapp.model.request.UpdateTaskStatusRequest
import com.example.voicecommandaiapp.network.ApiService
import com.example.voicecommandaiapp.model.Task
class TaskRepository(private val apiService: ApiService) {

    suspend fun getTasks(userId: Int, filter: String): TasksResponse {
        return apiService.getTasks(userId, filter)
    }

    suspend fun getTaskById(taskId: Int, userId: Int): TaskResponse {
        return apiService.getTaskById(taskId, userId)
    }

    suspend fun createTask(request: CreateTaskRequest): CreateTaskResponse {
        return apiService.createTask(request)
    }

    suspend fun updateTask(request: UpdateTaskRequest): SimpleResponse {
        return apiService.updateTask(request)
    }

    suspend fun deleteTask(taskId: Int, userId: Int): SimpleResponse {
        return apiService.deleteTask(
            DeleteTaskRequest(
                taskId = taskId,
                userId = userId
            )
        )
    }

    suspend fun updateTaskStatus(request: UpdateTaskStatusRequest): SimpleResponse {
        return apiService.updateTaskStatus(request)
    }
}
