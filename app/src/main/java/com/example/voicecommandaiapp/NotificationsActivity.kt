package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicecommandaiapp.adapter.NotificationsAdapter
import com.example.voicecommandaiapp.databinding.ActivityNotificationsBinding
import com.example.voicecommandaiapp.model.AppNotification
import com.example.voicecommandaiapp.model.NotificationItem
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.utils.SessionManager
import kotlinx.coroutines.launch

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: NotificationsAdapter

    private var allNotifications: List<NotificationItem> = emptyList()
    private var allTasks: List<Task> = emptyList()
    private var currentFilter = "ALL" // ALL, ACTIVITY, REMINDER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupTabs()
        loadNotifications()
        setupBottomNavigation()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ivSettingsGear.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.selectedItemId = R.id.navigation_notifications
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_tasks -> {
                    startActivity(Intent(this, MyTasksActivity::class.java))
                    true
                }
                R.id.navigation_notifications -> true
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // ---------------- RecyclerView ----------------

    private fun setupRecyclerView() {
        adapter = NotificationsAdapter { notification ->
            markAsRead(notification.id)
            handleNotificationClick(notification)
        }

        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = adapter
    }

    private fun handleNotificationClick(notification: NotificationItem) {
        if (notification.type == "REMINDER") {
            // Open Task Details (assuming reminder is linked to a task)
            // Ideally we'd have a taskId. For now, open generic MyTasks or find task logic.
            // Since we don't have task ID in NotificationItem easily mapped yet (API limit),
            // let's open MyTasksActivity as a safe fallback or a specific task if valid.
            startActivity(Intent(this, MyTasksActivity::class.java))
        } else {
            // Check if it's AI related
            if (notification.title.contains("AI", true)) {
                 startActivity(Intent(this, AiSuggestionsActivity::class.java))
            } else {
                // Default to MyTasks for other activity
                startActivity(Intent(this, MyTasksActivity::class.java))
            }
        }
    }

    // ---------------- Tabs ----------------

    private fun setupTabs() {
        binding.chipAllNotifications.setOnClickListener {
            currentFilter = "ALL"
            applyFilter()
        }

        binding.chipActivity.setOnClickListener {
            currentFilter = "ACTIVITY"
            applyFilter()
        }

        binding.chipReminders.setOnClickListener {
            currentFilter = "REMINDER"
            applyFilter()
        }
    }

    // ---------------- API ----------------

    private fun loadNotifications() {
        val userId = sessionManager.userId
        if (userId == -1) return

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val notifResponse = ApiClient.apiService.getNotifications(userId)
                val tasksResponse = ApiClient.apiService.getTasks(userId, "PENDING")

                if (notifResponse.ok) {
                    allNotifications = notifResponse.data?.map { it.toNotificationItem() } ?: emptyList()
                }
                
                allTasks = tasksResponse.tasks ?: emptyList()
                
                applyFilter()

            } catch (e: Exception) {
                Toast.makeText(this@NotificationsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun applyFilter() {
        val taskItems = allTasks.map { it.toNotificationItem() }
        
        val filtered = when (currentFilter) {
            "ACTIVITY" -> allNotifications.filter { it.type == "ACTIVITY" }
            "REMINDER" -> {
                val reminders = allNotifications.filter { it.type == "REMINDER" }
                (reminders + taskItems).sortedByDescending { it.time }
            }
            else -> {
                // ALL
                (allNotifications + taskItems).sortedByDescending { it.time }
            }
        }
        
        val groupedList = groupNotifications(filtered)
        adapter.submitList(groupedList)
        
        // Update subtitle count
        val unreadCount = filtered.count { it.isUnread }
        binding.tvNotificationSubtitle.text = "$unreadCount unread"
    }

    // ---------------- Grouping Logic ----------------

    private fun groupNotifications(items: List<NotificationItem>): List<com.example.voicecommandaiapp.adapter.NotificationListItem> {
        val groupedItems = mutableListOf<com.example.voicecommandaiapp.adapter.NotificationListItem>()

        if (currentFilter == "ALL") {
            // Group by Date: Today, Yesterday, This Week, Older
            val today = java.time.LocalDate.now()
            val yesterday = today.minusDays(1)
            val oneWeekAgo = today.minusWeeks(1)

            val todayList = mutableListOf<NotificationItem>()
            val yesterdayList = mutableListOf<NotificationItem>()
            val thisWeekList = mutableListOf<NotificationItem>()
            val olderList = mutableListOf<NotificationItem>()

            for (item in items) {
                val date = parseDate(item.time)
                when {
                    date.isEqual(today) -> todayList.add(item)
                    date.isEqual(yesterday) -> yesterdayList.add(item)
                    date.isAfter(oneWeekAgo) -> thisWeekList.add(item)
                    else -> olderList.add(item)
                }
            }

            if (todayList.isNotEmpty()) {
                groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Header("Today", todayList.size))
                todayList.forEach { groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Notification(it)) }
            }
            if (yesterdayList.isNotEmpty()) {
                groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Header("Yesterday", yesterdayList.size))
                yesterdayList.forEach { groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Notification(it)) }
            }
            if (thisWeekList.isNotEmpty()) {
                groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Header("This Week", thisWeekList.size))
                thisWeekList.forEach { groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Notification(it)) }
            }
            if (olderList.isNotEmpty()) {
                groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Header("Older", olderList.size))
                olderList.forEach { groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Notification(it)) }
            }

        } else if (currentFilter == "ACTIVITY") {
            groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Header("Recent Activity", items.size))
            items.forEach { groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Notification(it)) }
        } else {
            groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Header("Upcoming Reminders", items.size))
            items.forEach { groupedItems.add(com.example.voicecommandaiapp.adapter.NotificationListItem.Notification(it)) }
        }

        return groupedItems
    }

    private fun parseDate(dateStr: String): java.time.LocalDate {
        // Simple parser assuming "yyyy-MM-dd HH:mm:ss" or "Just now" fallback
        if (dateStr == "Just now") return java.time.LocalDate.now()
        return try {
            val dateTime = java.time.LocalDateTime.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            dateTime.toLocalDate()
        } catch (e: Exception) {
            java.time.LocalDate.now().minusYears(1) // Treat as older if parse fails
        }
    }


    // ---------------- Mapper ----------------

    private fun AppNotification.toNotificationItem(): NotificationItem {
        val safeMessage = message ?: "New Notification"
        // Determine type based on keywords
        val isReminder = safeMessage.contains("reminder", ignoreCase = true) || safeMessage.contains("remind", ignoreCase = true)
        val type = if (isReminder) "REMINDER" else "ACTIVITY"
        
        // Titles and Icons
        val title = if (isReminder) "Upcoming Reminder" else "Recent Activity"
        val icon = if (isReminder) android.R.drawable.ic_popup_reminder else android.R.drawable.ic_menu_agenda
        
        // Improve Time Label (e.g. "Just now" vs actual time)
        // For simplicity reusing created_at. Ideally convert to "2 min ago" here.
        
        return NotificationItem(
            id = id,
            title = title,
            subtitle = safeMessage,
            time = created_at ?: "Just now",
            isUnread = is_read ?: false,
            iconRes = icon,
            backgroundRes = 0,
            type = type
        )
    }

    // ---------------- Mark Read ----------------

    private fun markAsRead(notificationId: Int) {
        lifecycleScope.launch {
            try {
                ApiClient.apiService.markNotificationRead(notificationId)
            } catch (_: Exception) { }
        }
    }

    private fun Task.toNotificationItem(): NotificationItem {
        val displayTime = if (!task_date.isNullOrBlank() && !task_time.isNullOrBlank()) {
             "$task_date $task_time"
        } else {
             task_date ?: "Upcoming"
        }
        
        return NotificationItem(
            id = -id, // Use negative ID to avoid collision with notification table IDs
            title = title ?: "Untitled Task",
            subtitle = "Scheduled for $displayTime",
            time = displayTime,
            isUnread = true, // Force highlight
            iconRes = android.R.drawable.ic_popup_reminder,
            backgroundRes = 0,
            type = "REMINDER"
        )
    }
}
