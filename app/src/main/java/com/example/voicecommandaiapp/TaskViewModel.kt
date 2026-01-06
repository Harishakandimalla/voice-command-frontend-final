package com.example.voicecommandaiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicecommandaiapp.model.CreateTaskResponse
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.model.request.CreateTaskRequest
import com.example.voicecommandaiapp.model.request.UpdateTaskRequest
import com.example.voicecommandaiapp.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _taskCreationResult = MutableLiveData<Result<CreateTaskResponse>>()
    val taskCreationResult: LiveData<Result<CreateTaskResponse>> = _taskCreationResult

    private val _task = MutableLiveData<Task?>()
    val task: LiveData<Task?> = _task

    private val _tasks = MutableLiveData<List<Task>?>()
    val tasks: LiveData<List<Task>?> = _tasks

    private val _taskUpdated = MutableLiveData<Boolean>()
    val taskUpdated: LiveData<Boolean> = _taskUpdated

    private val _taskDeleted = MutableLiveData<Boolean>()
    val taskDeleted: LiveData<Boolean> = _taskDeleted

    fun getTasks(userId: Int, filter: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTasks(userId, filter)
                _tasks.postValue(response.tasks)
            } catch (e: Exception) {
                _tasks.postValue(null)
            }
        }
    }

    fun createTask(request: CreateTaskRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createTask(request)
                _taskCreationResult.postValue(Result.success(response))
            } catch (e: Exception) {
                _taskCreationResult.postValue(Result.failure(e))
            }
        }
    }

    fun getTaskById(taskId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getTaskById(taskId, userId)
                if (response.ok) {
                    _task.postValue(response.tasks.firstOrNull())
                } else {
                    _task.postValue(null)
                }
            } catch (e: Exception) {
                _task.postValue(null)
            }
        }
    }

    fun updateTask(request: UpdateTaskRequest) {
        viewModelScope.launch {
            try {
                val response = repository.updateTask(request)
                _taskUpdated.postValue(response.ok)
            } catch (e: Exception) {
                _taskUpdated.postValue(false)
            }
        }
    }

    fun deleteTask(taskId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteTask(taskId, userId)
                _taskDeleted.postValue(response.ok)
            } catch (e: Exception) {
                _taskDeleted.postValue(false)
            }
        }
    }

    fun completeTask(taskId: Int) {
        // TaskManager.completeTask(taskId)
        // TODO: Implement this using the repository
    }
}
