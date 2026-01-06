package com.example.voicecommandaiapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.voicecommandaiapp.databinding.ActivityTaskCreatedBinding
import com.example.voicecommandaiapp.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.voicecommandaiapp.model.request.CreateTaskRequest
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.repository.TaskRepository
import com.example.voicecommandaiapp.utils.SessionManager

class TaskCreatedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskCreatedBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(ApiClient.apiService))
    }
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskCreatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)

        bindTaskData()
        saveTaskToBackend()
        observeViewModel()
    }

    private fun bindTaskData() {
        val task = intent.getSerializableExtra("NEW_TASK") as? Task

        if (task != null) {
            binding.tvTaskTitleCreated.text = task.title ?: "Untitled Task"
            binding.tvTaskDateCreated.text = formatDate(task.task_date)
            binding.tvTaskTimeCreated.text = formatTime(task.task_time)

            val cat = task.category ?: "General"
            val pri = task.priority?.replaceFirstChar { it.uppercase() } ?: "Normal"
            binding.tvTaskCategoryCreated.text = "$cat · $pri Priority"
        }
    }
    
    private fun saveTaskToBackend() {
        val task = intent.getSerializableExtra("NEW_TASK") as? Task
        val userId = sessionManager.userId

        if (task != null && userId > 0) {
            val request = CreateTaskRequest(
                userId = userId,
                title = task.title ?: "Untitled",
                description = task.description,
                taskDate = task.task_date ?: "",
                taskTime = task.task_time,
                category = task.category ?: "General",
                priority = task.priority ?: "MEDIUM"
            )
            taskViewModel.createTask(request)
        } else {
             Toast.makeText(this, "Error: Invalid task data", Toast.LENGTH_SHORT).show()
             finish()
        }
    }

    private fun observeViewModel() {
        taskViewModel.taskCreationResult.observe(this) { result ->
            result.onSuccess {
                // Wait briefly then close
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 2000)
            }.onFailure { e ->
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                // Don't close automatically on error so user sees it
            }
        }
    }

    // ---------------- Formatting ----------------

    private fun formatDate(date: String?): String {
        if (date.isNullOrBlank()) return "No date"
        return try {
            val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outFmt = SimpleDateFormat("MMMM dd", Locale.US)
            outFmt.format(inFmt.parse(date)!!)
        } catch (e: Exception) {
            date
        }
    }

    private fun formatTime(time: String?): String {
        if (time == null) return "No time"
        return try {
            val inFmt = SimpleDateFormat("HH:mm:ss", Locale.US)
            val outFmt = SimpleDateFormat("hh:mm a", Locale.US)
            outFmt.format(inFmt.parse(time)!!)
        } catch (e: Exception) {
            time
        }
    }
}
