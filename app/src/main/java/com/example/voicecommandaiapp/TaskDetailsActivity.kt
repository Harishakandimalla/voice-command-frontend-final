package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voicecommandaiapp.databinding.ActivityTaskDetailsBinding
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.model.request.UpdateTaskStatusRequest
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding
    private lateinit var sessionManager: SessionManager

    private var taskId: Int = -1
    private var userId: Int = -1
    private var currentTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            sessionManager = SessionManager(this)
            userId = sessionManager.userId
            taskId = intent.getIntExtra("TASK_ID", -1)

            // 🚫 SAFETY CHECK
            if (userId <= 0 || taskId <= 0) {
                Toast.makeText(this, "Invalid task data", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            setupToolbar()
            setupButtons()
            setupStaticLabels()

            // 🚀 FAST LOAD: Check if entire task object was passed
            val taskObj = intent.getSerializableExtra("TASK_OBJ") as? Task
            if (taskObj != null) {
                bindTask(taskObj)
            } else {
                loadTaskDetails()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ---------------- TOOLBAR ----------------
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    // ---------------- STATIC LABELS ----------------
    private fun setupStaticLabels() {
        binding.layoutDate.label.text = "Date"
        binding.layoutTime.label.text = "Time"
        binding.layoutReminder.label.text = "Reminder"
        binding.layoutRepeat.label.text = "Recurrence"
    }

    // ---------------- LOAD TASK ----------------
    private fun loadTaskDetails() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getTaskById(
                    taskId = taskId,
                    userId = userId
                )

                // 🛡 STRICT API CHECK
                if (!response.ok || response.tasks.isNullOrEmpty()) {
                    Toast.makeText(
                        this@TaskDetailsActivity,
                        "Task not found: ID=$taskId, User=$userId",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }

                // Verify valid task
                val task = response.tasks.first()
                bindTask(task)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@TaskDetailsActivity,
                    "Failed to load task",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    // ---------------- BIND DATA ----------------
    private fun bindTask(task: Task) = with(binding) {
        currentTask = task

        tvTaskTitle.text =
            task.title?.takeIf { it.isNotBlank() } ?: "Untitled Task"

        tvTaskDescription.text =
            task.description?.takeIf { it.isNotBlank() }
                ?: "No description"

        chipCategory.text =
            task.category ?: "General"

        chipPriority.text =
            task.priority
                ?.lowercase()
                ?.replaceFirstChar { it.uppercase() }
                ?: "Normal"

        layoutDate.value.text =
            formatDate(task.task_date)

        layoutTime.value.text =
            formatTime(task.task_time)

        layoutReminder.value.text =
            task.reminder?.takeIf { it.isNotBlank() } ?: "No reminder"

        layoutRepeat.value.text =
            task.repeat?.takeIf { it.isNotBlank() } ?: "No recurrence"

        // Hide complete button if already completed
        btnComplete.visibility =
            if (task.status == "COMPLETED") View.GONE else View.VISIBLE
    }

    // ---------------- BUTTONS ----------------
    private fun setupButtons() {

        binding.btnComplete.setOnClickListener {
            markTaskCompleted()
        }

        binding.btnEdit.setOnClickListener {
            startActivity(
                Intent(this, EditTaskActivity::class.java)
                    .putExtra("TASK_ID", taskId)
                    .putExtra("TASK_OBJ", currentTask)
            )
        }
    }

    // ---------------- COMPLETE TASK ----------------
    private fun markTaskCompleted() {
        lifecycleScope.launch {
            try {
                val request = UpdateTaskStatusRequest(
                    taskId = taskId,
                    userId = userId,
                    status = "COMPLETED"
                )

                val response =
                    ApiClient.apiService.updateTaskStatus(request)

                if (response.ok) {
                    Toast.makeText(
                        this@TaskDetailsActivity,
                        "Task marked as completed",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@TaskDetailsActivity,
                        "Failed to update task",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@TaskDetailsActivity,
                    "Error completing task",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------- FORMATTERS ----------------
    private fun formatDate(date: String?): String {
        return try {
            if (date.isNullOrBlank()) "No date"
            else {
                val input =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val output =
                    SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                output.format(input.parse(date)!!)
            }
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    private fun formatTime(time: String?): String {
        return try {
            if (time.isNullOrBlank()) "No time"
            else {
                val input =
                    SimpleDateFormat("HH:mm", Locale.getDefault())
                val output =
                    SimpleDateFormat("h:mm a", Locale.getDefault())
                output.format(input.parse(time)!!)
            }
        } catch (e: Exception) {
            "Invalid time"
        }
    }
}
