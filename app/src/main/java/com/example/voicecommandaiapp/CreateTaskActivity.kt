package com.example.voicecommandaiapp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.voicecommandaiapp.databinding.ActivityCreateTaskBinding
import com.example.voicecommandaiapp.model.request.CreateTaskRequest
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.repository.TaskRepository
import com.example.voicecommandaiapp.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTaskBinding

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(ApiClient.apiService))
    }

    private lateinit var sessionManager: SessionManager
    private var userId: Int = -1

    private val selectedDate: Calendar = Calendar.getInstance()
    private val selectedTime: Calendar = Calendar.getInstance()
    private var selectedCategory: String = ""
    private var selectedPriority: String = ""

    // ---------------- ACTIVITY RESULT LAUNCHERS ----------------

    private val categoryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedCategory =
                    result.data?.getStringExtra("SELECTED_CATEGORY") ?: ""
                binding.rowCategory.tvActionSubtitle.text = selectedCategory
            }
        }

    private val priorityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedPriority =
                    result.data?.getStringExtra("SELECTED_PRIORITY") ?: ""
                binding.rowPriority.tvActionSubtitle.text = selectedPriority
            }
        }

    // ---------------- LIFECYCLE ----------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userId = sessionManager.userId

        if (userId == -1) {
            Toast.makeText(
                this,
                "User not logged in. Please restart the app.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setupActionRows()
        setupClickListeners()
        observeViewModel()
    }

    // ---------------- UI SETUP ----------------

    private fun setupActionRows() {

        binding.rowDate.ivActionIcon.setImageResource(R.drawable.ic_calendar)
        binding.rowDate.tvActionTitle.text = "Date"
        updateDateInView()

        binding.rowTime.ivActionIcon.setImageResource(R.drawable.ic_clock)
        binding.rowTime.tvActionTitle.text = "Time"
        updateTimeInView()

        binding.rowCategory.ivActionIcon.setImageResource(R.drawable.ic_task_category)
        binding.rowCategory.tvActionTitle.text = "Category"
        binding.rowCategory.tvActionSubtitle.text = "Work" // default

        binding.rowPriority.ivActionIcon.setImageResource(R.drawable.ic_priority)
        binding.rowPriority.tvActionTitle.text = "Priority"
        binding.rowPriority.tvActionSubtitle.text = "MEDIUM" // default
    }

    private fun setupClickListeners() {

        binding.btnCreateTask.setOnClickListener {
            createTask()
        }

        binding.tvCancel.setOnClickListener {
            finish()
        }

        binding.rowDate.root.setOnClickListener {
            showDatePicker()
        }

        binding.rowTime.root.setOnClickListener {
            showTimePicker()
        }

        binding.rowCategory.root.setOnClickListener {
            categoryLauncher.launch(
                Intent(this, CategoriesActivity::class.java)
            )
        }

        binding.rowPriority.root.setOnClickListener {
            priorityLauncher.launch(
                Intent(this, SetPriorityActivity::class.java)
            )
        }
    }

    // ---------------- VIEWMODEL OBSERVER ----------------

    private fun observeViewModel() {
        taskViewModel.taskCreationResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.ok) {
                    if (response.taskId != null) {
                        scheduleAlarm(response.taskId)
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        response.error ?: "Failed to create task",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.onFailure { e ->
                Toast.makeText(
                    this,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("CreateTaskActivity", "Exception", e)
            }
        }
    }

    // ---------------- DATE & TIME ----------------

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate.set(year, month, day)
                updateDateInView()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                selectedTime.set(Calendar.MINUTE, minute)
                updateTimeInView()
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateDateInView() {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
        binding.rowDate.tvActionSubtitle.text =
            formatter.format(selectedDate.time)
    }

    private fun updateTimeInView() {
        val formatter = SimpleDateFormat("HH:mm", Locale.US)
        binding.rowTime.tvActionSubtitle.text =
            formatter.format(selectedTime.time)
    }

    // ---------------- CREATE TASK ----------------

    private fun createTask() {

        val title = binding.etTaskTitleCreate.text.toString().trim()
        val description = binding.etTaskDescriptionCreate.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ DEFAULTS
        val finalCategory =
            if (selectedCategory.isEmpty()) "Work" else selectedCategory

        val finalPriority =
            if (selectedPriority.isEmpty()) "MEDIUM"
            else selectedPriority.uppercase(Locale.US)

        val apiDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val apiTimeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

        val taskDate = apiDateFormatter.format(selectedDate.time)
        val taskTime = apiTimeFormatter.format(selectedTime.time)

        val request = CreateTaskRequest(
            userId = userId,
            title = title,
            description = description,
            taskDate = taskDate,
            taskTime = taskTime,
            category = finalCategory,
            priority = finalPriority
        )

        taskViewModel.createTask(request)
    }
    private fun scheduleAlarm(taskId: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH),
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            0
        )
        
        NotificationScheduler.scheduleTaskReminder(
            this,
            taskId,
            binding.etTaskTitleCreate.text.toString(),
            calendar.timeInMillis
        )
    }
}
