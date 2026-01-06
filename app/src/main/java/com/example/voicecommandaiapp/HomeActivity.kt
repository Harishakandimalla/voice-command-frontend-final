package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicecommandaiapp.adapter.HomeTaskAdapter
import com.example.voicecommandaiapp.databinding.ActivityHomeBinding
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.utils.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: HomeTaskAdapter
    private lateinit var sessionManager: SessionManager

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        userId = sessionManager.userId

        // 🚫 Not logged in → Login
        if (userId <= 0) {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 👤 Username
        binding.tvUserName.text =
            sessionManager.userName.ifBlank { "User" }

        setupRecycler()
        setupTopActions()
        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    // ---------------- Recycler ----------------
    private fun setupRecycler() {
        adapter = HomeTaskAdapter { task ->
            if (task.id > 0) {
                startActivity(
                    Intent(this, TaskDetailsActivity::class.java)
                        .putExtra("TASK_ID", task.id)
                        .putExtra("TASK_OBJ", task)
                )
            }
        }

        binding.homeRecycler.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = this@HomeActivity.adapter
            setHasFixedSize(true)
        }
    }

    // ---------------- Top Icons ----------------
    private fun setupTopActions() {

        // 1. Chat Icon -> AI Assistant
        binding.ivChat.setOnClickListener {
            startActivity(Intent(this, AiAssistantActivity::class.java))
        }

        // 2. Notification Icon -> Notifications
        binding.ivNotification.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // 3. Mic Icon -> Listening Screen
        binding.btnMic.setOnClickListener {
            startActivity(Intent(this, ListeningActivity::class.java))
        }

        // 4. AI Suggestion Card -> AI Suggestions
        binding.aiSuggestionCard.root.setOnClickListener {
            startActivity(Intent(this, AiSuggestionsActivity::class.java))
        }
    }

    // ---------------- Bottom Navigation ----------------
    private fun setupBottomNav() {

        binding.bottomNavigationView.selectedItemId = R.id.navigation_home

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> true // already here

                R.id.navigation_tasks -> {
                    startActivity(
                        Intent(this, MyTasksActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    )
                    true
                }

                R.id.navigation_notifications -> {
                    startActivity(
                        Intent(this, NotificationsActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    )
                    true
                }

                R.id.navigation_settings -> {
                    startActivity(
                        Intent(this, SettingsActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    )
                    true
                }

                else -> false
            }
        }
    }

    // ---------------- Load Tasks ----------------
    private fun loadTasks() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getTasks(userId, "all")
                applyLogic(response.tasks)
            } catch (e: Exception) {
                Log.e("HOME", "Failed to load tasks", e)
                applyLogic(emptyList())
            }
        }
    }

    // ---------------- CORE HOME LOGIC ----------------
    private fun applyLogic(tasks: List<Task>) {

        val today = LocalDate.now()

        val todayList = mutableListOf<Task>()
        val upcomingList = mutableListOf<Task>()
        val pendingList = mutableListOf<Task>()
        val completedList = mutableListOf<Task>()

        for (task in tasks) {

            val taskDate = try {
                task.task_date?.takeIf { it.isNotBlank() }
                    ?.let { LocalDate.parse(it) }
            } catch (e: Exception) {
                null
            }

            when {
                task.status == "COMPLETED" ->
                    completedList.add(task)

                taskDate == null ->
                    pendingList.add(task)

                taskDate.isEqual(today) ->
                    todayList.add(task)

                taskDate.isAfter(today) ->
                    upcomingList.add(task)

                taskDate.isBefore(today) ->
                    pendingList.add(task)
            }
        }

        // 🧠 Update adapter
        adapter.updateSections(
            todayList,
            upcomingList,
            pendingList,
            completedList
        )

        // 🔢 Update counts
        binding.statToday.tvTodayCount.text = todayList.size.toString()
        binding.statUpcoming.tvUpcomingCount.text = upcomingList.size.toString()
        binding.statPending.tvPendingCount.text = pendingList.size.toString()
        binding.statCompleted.tvCompletedCount.text = completedList.size.toString()

        // 🟢 Empty state handling (optional)
        binding.homeRecycler.visibility =
            if (tasks.isEmpty()) View.GONE else View.VISIBLE
    }
}
