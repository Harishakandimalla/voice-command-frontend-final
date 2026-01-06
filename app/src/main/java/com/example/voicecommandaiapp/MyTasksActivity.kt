package com.example.voicecommandaiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicecommandaiapp.adapter.TaskAdapter
import com.example.voicecommandaiapp.databinding.ActivityMyTasksBinding
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.repository.TaskRepository
import com.example.voicecommandaiapp.utils.SessionManager
import com.google.android.material.chip.Chip

class MyTasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyTasksBinding

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(ApiClient.apiService))
    }

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var sessionManager: SessionManager

    private var userId: Int = -1
    private var currentFilter = "today"

    private val taskChangeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                fetchTasks()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Session
        sessionManager = SessionManager(this)
        userId = sessionManager.userId

        // 🚫 Not logged in
        if (userId <= 0) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
            return
        }

        setupRecyclerView()
        setupFilters()
        setupClicks()
        setupBottomNav()
        observeTasks()

        binding.chipToday.isChecked = true
        fetchTasks()
    }

    // ---------------- Recycler ----------------
    private fun setupRecyclerView() {

        taskAdapter = TaskAdapter(emptyList()) { task ->

            // 🛡 SAFETY CHECK
            if (task.id <= 0) {
                Toast.makeText(this, "Invalid task", Toast.LENGTH_SHORT).show()
                return@TaskAdapter
            }

            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            intent.putExtra("TASK_OBJ", task)
            taskChangeLauncher.launch(intent)
        }

        binding.rvMyTasks.apply {
            layoutManager = LinearLayoutManager(this@MyTasksActivity)
            adapter = taskAdapter
            setHasFixedSize(true)
        }
    }

    // ---------------- Filters ----------------
    private fun setupFilters() {

        binding.chipGroupFilter.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)

            currentFilter = when (chip?.id) {
                R.id.chip_today -> "today"
                R.id.chip_upcoming -> "upcoming"
                R.id.chip_completed -> "completed"
                R.id.chip_all -> "all"
                else -> "today"
            }

            fetchTasks()
        }
    }

    // ---------------- Clicks ----------------
    private fun setupClicks() {

        // ➕ Create Task
        binding.ivAddTask.setOnClickListener {
            taskChangeLauncher.launch(
                Intent(this, CreateTaskActivity::class.java)
            )
        }

        binding.ivCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        
        // 📊 Category Summary
        binding.ivCategorySummary.setOnClickListener {
            val currentTasks = taskViewModel.tasks.value ?: emptyList()
            if (currentTasks.isNotEmpty()) {
                val sheet = CategorySummaryBottomSheet.newInstance(ArrayList(currentTasks))
                sheet.show(supportFragmentManager, "CategorySummary")
            } else {
                Toast.makeText(this, "No tasks to summarize", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------- Data ----------------
    private fun fetchTasks() {
        Log.d("MyTasksActivity", "Fetching tasks → $currentFilter")
        taskViewModel.getTasks(userId, currentFilter)
    }

    private fun observeTasks() {
        taskViewModel.tasks.observe(this) { tasks ->
            val safeTasks = tasks ?: emptyList()
            taskAdapter.updateTasks(safeTasks, currentFilter)
            
            updateTopSection(safeTasks)
        }
    }
    
    private fun updateTopSection(tasks: List<com.example.voicecommandaiapp.model.Task>) {
        val totalTasks = tasks.size
        
        // "Completed Today" logic
        // We need to parse task_date and check if status is completed
        val today = java.time.LocalDate.now()
        var completedTodayCount = 0
        var completedTotalCount = 0
        
        for (t in tasks) {
            if (t.status == "COMPLETED") {
                completedTotalCount++
                
                // Parse date
                try {
                    if (!t.task_date.isNullOrBlank()) {
                         val d = java.time.LocalDate.parse(t.task_date)
                         if (d.isEqual(today)) {
                             completedTodayCount++
                         }
                    }
                } catch (e: Exception) { /* ignore */ }
            }
        }
        
        // 1. Task Subtitle
        binding.tvTaskSubtitle.text = "$totalTasks tasks • $completedTodayCount completed today"
        
        // 2. Productivity Score (Completed / Total)
        val score = if (totalTasks > 0) (completedTotalCount.toFloat() / totalTasks.toFloat() * 100).toInt() else 0
        
        // Access included layout views safely
        // Note: included layout does not have an ID, so we find by ID from root
        val tvScoreValue = findViewById<android.widget.TextView>(R.id.tv_productivity_score_value)
        val progressProductivity = findViewById<android.widget.ProgressBar>(R.id.progress_productivity)
        
        if (tvScoreValue != null && progressProductivity != null) {
            tvScoreValue.text = "$score%"
            progressProductivity.progress = score
        }
    }

    // ---------------- Bottom Nav ----------------
    private fun setupBottomNav() {

        binding.bottomNavigationView.selectedItemId =
            R.id.navigation_tasks

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            if (item.itemId == R.id.navigation_tasks) {
                return@setOnItemSelectedListener true
            }

            val intent = when (item.itemId) {

                R.id.navigation_home ->
                    Intent(this, HomeActivity::class.java)

                R.id.navigation_notifications ->
                    Intent(this, NotificationsActivity::class.java)

                R.id.navigation_settings ->
                    Intent(this, SettingsActivity::class.java)

                else -> null
            }

            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(it)
            }
            true
        }
    }
}
