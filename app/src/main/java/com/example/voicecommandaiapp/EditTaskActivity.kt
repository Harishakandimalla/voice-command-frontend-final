package com.example.voicecommandaiapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.voicecommandaiapp.databinding.ActivityEditTaskBinding
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.model.request.UpdateTaskRequest
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.repository.TaskRepository
import com.example.voicecommandaiapp.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(ApiClient.apiService))
    }
    private var taskId: Int = -1
    private var userId: Int = -1
    private val calendar = Calendar.getInstance()

    private var selectedCategory: String = ""
    private var selectedPriority: String = ""

    private val categoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedCategory = result.data?.getStringExtra("SELECTED_CATEGORY") ?: ""
            binding.cardEditCategory.tvEditCardValue.text = selectedCategory
        }
    }

    private val priorityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedPriority = result.data?.getStringExtra("SELECTED_PRIORITY") ?: ""
            binding.cardEditPriority.tvEditCardValue.text = selectedPriority
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        userId = sessionManager.userId

        taskId = intent.getIntExtra("TASK_ID", -1)

        if (taskId == -1 || userId == -1) {
            Toast.makeText(this, "Invalid Task or User ID", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupClickListeners()
        observeViewModel()
        setupClickListeners()
        observeViewModel()

        // 🚀 FAST LOAD
        val taskObj = intent.getSerializableExtra("TASK_OBJ") as? Task
        if (taskObj != null) {
            binding.progressBar.visibility = View.GONE
            populateTaskDetails(taskObj)
            binding.contentGroup.visibility = View.VISIBLE
        } else {
            fetchTaskDetails()
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveChanges.setOnClickListener { updateTask() }
        binding.tvDeleteTask.setOnClickListener { deleteTask() }
        binding.toolbarEditTask.setNavigationOnClickListener { finish() }

        binding.cardEditDate.root.setOnClickListener { showDatePicker() }
        binding.cardEditTime.root.setOnClickListener { showTimePicker() }
        binding.cardEditCategory.root.setOnClickListener {
            categoryLauncher.launch(Intent(this, CategoriesActivity::class.java))
        }
        binding.cardEditPriority.root.setOnClickListener {
            priorityLauncher.launch(Intent(this, SetPriorityActivity::class.java))
        }
    }

    private fun observeViewModel() {
        taskViewModel.task.observe(this) { task ->
            binding.progressBar.visibility = View.GONE
            if (task != null) {
                populateTaskDetails(task)
                binding.contentGroup.visibility = View.VISIBLE
            } else {
                showError("Failed to load task details.")
            }
        }

        taskViewModel.taskUpdated.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                // Reschedule reminder
                val timeInMillis = calendar.timeInMillis
                NotificationScheduler.scheduleTaskReminder(
                    this,
                    taskId,
                    binding.etTitle.text.toString(),
                    timeInMillis
                )
                setResult(RESULT_OK) // Notify previous screen
                finish()
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.taskDeleted.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                NotificationScheduler.cancelTaskReminder(this, taskId)
                setResult(RESULT_OK) // Notify previous screen
                finish()
            } else {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchTaskDetails() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentGroup.visibility = View.GONE
        taskViewModel.getTaskById(taskId, userId)
    }

    private fun populateTaskDetails(task: Task) {
        binding.etTitle.setText(task.title)
        binding.etDescription.setText(task.description)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            task.task_date?.let { dateStr ->
                val taskDate = sdf.parse(dateStr)
                if (taskDate != null) {
                    calendar.time = taskDate
                }
            }
        } catch (e: Exception) { /* Ignore */ }
        updateDateInView()

        val timeSdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        try {
            task.task_time?.let { timeStr ->
                val taskTime = timeSdf.parse(timeStr)
                if (taskTime != null) {
                    val timeCal = Calendar.getInstance()
                    timeCal.time = taskTime
                    calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                    calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                }
            }
        } catch (e: Exception) { /* Ignore */ }
        updateTimeInView()

        binding.cardEditCategory.tvEditCardValue.text = task.category ?: "General"
        binding.cardEditPriority.tvEditCardValue.text = task.priority ?: "Normal"

        selectedCategory = task.category ?: ""
        selectedPriority = task.priority ?: ""
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateTimeInView()
        }
        TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.cardEditDate.tvEditCardValue.text = sdf.format(calendar.time)
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm:ss"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.cardEditTime.tvEditCardValue.text = sdf.format(calendar.time)
    }

    private fun updateTask() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (title.isEmpty() || selectedCategory.isEmpty() || selectedPriority.isEmpty()) {
            Toast.makeText(this, "Title, category, and priority are required", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateTaskRequest(
            taskId = taskId,
            userId = userId,
            title = title,
            description = description,
            taskDate = binding.cardEditDate.tvEditCardValue.text.toString(),
            taskTime = binding.cardEditTime.tvEditCardValue.text.toString(),
            category = selectedCategory,
            priority = selectedPriority.uppercase(Locale.US)
        )

        taskViewModel.updateTask(request)
    }

    private fun deleteTask() {
        taskViewModel.deleteTask(taskId, userId)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
    }
}
