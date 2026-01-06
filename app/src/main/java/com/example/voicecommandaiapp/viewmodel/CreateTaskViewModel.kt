package com.example.voicecommandaiapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicecommandaiapp.model.request.CreateTaskRequest
import com.example.voicecommandaiapp.repository.TaskRepository
import kotlinx.coroutines.launch

class CreateTaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    fun createTask(req: CreateTaskRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = taskRepository.createTask(req)
                Log.d("TASK_CREATE", "Response = $res")

                if (res.ok && res.taskId != null) {
                    onSuccess()
                } else {
                    Log.e("TASK_CREATE", "Create failed")
                }
            } catch (e: Exception) {
                Log.e("TASK_CREATE", "Error", e)
            }
        }
    }
}
