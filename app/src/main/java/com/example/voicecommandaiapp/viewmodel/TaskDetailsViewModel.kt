package com.example.voicecommandaiapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.model.TaskStatus
import com.example.voicecommandaiapp.model.request.UpdateTaskStatusRequest
import com.example.voicecommandaiapp.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskDetailsViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _task = MutableLiveData<Task?>()
    val task: LiveData<Task?> = _task

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _taskUpdated = MutableLiveData<Boolean>()
    val taskUpdated: LiveData<Boolean> = _taskUpdated

    fun fetchTask(taskId: Int, userId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getTaskById(taskId, userId)
                if (response.ok && response.tasks.isNotEmpty()) {
                    _task.postValue(response.tasks.first())
                } else {
                    _error.postValue("Could not find the specified task.")
                }
            } catch (e: Exception) {
                _error.postValue("An error occurred while fetching the task: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateTaskStatus(taskId: Int, userId: Int, status: TaskStatus) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = UpdateTaskStatusRequest(taskId, userId, status.name)
                val response = repository.updateTaskStatus(request)
                if (response.ok) {
                    _taskUpdated.postValue(true)
                } else {
                    _error.postValue("Failed to update task status.")
                }
            } catch (e: Exception) {
                _error.postValue("An error occurred: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
