package com.example.voicecommandaiapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notification Settings"

        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)

        setupPushNotifications()
        setupPreferences()
        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        val selectedSound = sharedPreferences.getString("notification_sound", "Default")
        val notificationSoundView = findViewById<View>(R.id.notification_sound)
        notificationSoundView.findViewById<TextView>(R.id.item_value).text = selectedSound
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupPushNotifications() {
        // AI Suggestions
        val aiSuggestionsView = findViewById<View>(R.id.ai_suggestions)
        aiSuggestionsView.findViewById<TextView>(R.id.item_title).text = "AI Suggestions"
        aiSuggestionsView.findViewById<TextView>(R.id.item_subtitle).text = "Get tips for better productivity."
        val aiSuggestionsToggle = aiSuggestionsView.findViewById<SwitchMaterial>(R.id.item_toggle)
        aiSuggestionsToggle.isChecked = sharedPreferences.getBoolean("ai_suggestions", true)
        aiSuggestionsToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("ai_suggestions", isChecked).apply()
        }

        // Task Reminders
        val taskRemindersView = findViewById<View>(R.id.task_reminders)
        taskRemindersView.findViewById<TextView>(R.id.item_title).text = "Task Reminders"
        taskRemindersView.findViewById<TextView>(R.id.item_subtitle).text = "Reminders for upcoming tasks."
        val taskRemindersToggle = taskRemindersView.findViewById<SwitchMaterial>(R.id.item_toggle)
        taskRemindersToggle.isChecked = sharedPreferences.getBoolean("task_reminders", true)
        taskRemindersToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("task_reminders", isChecked).apply()
        }

        // Activity Updates
        val activityUpdatesView = findViewById<View>(R.id.activity_updates)
        activityUpdatesView.findViewById<TextView>(R.id.item_title).text = "Activity Updates"
        activityUpdatesView.findViewById<TextView>(R.id.item_subtitle).text = "When tasks are completed or changed."
        val activityUpdatesToggle = activityUpdatesView.findViewById<SwitchMaterial>(R.id.item_toggle)
        activityUpdatesToggle.isChecked = sharedPreferences.getBoolean("activity_updates", false)
        activityUpdatesToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("activity_updates", isChecked).apply()
        }

        // New Features
        val newFeaturesView = findViewById<View>(R.id.new_features)
        newFeaturesView.findViewById<TextView>(R.id.item_title).text = "New Features"
        newFeaturesView.findViewById<TextView>(R.id.item_subtitle).text = "Updates on new app features."
        val newFeaturesToggle = newFeaturesView.findViewById<SwitchMaterial>(R.id.item_toggle)
        newFeaturesToggle.isChecked = sharedPreferences.getBoolean("new_features", true)
        newFeaturesToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("new_features", isChecked).apply()
        }
    }

    private fun setupPreferences() {
        // Notification Sound
        val notificationSoundView = findViewById<View>(R.id.notification_sound)
        notificationSoundView.findViewById<TextView>(R.id.item_title).text = "Notification Sound"
        notificationSoundView.setOnClickListener {
            val intent = Intent(this, NotificationSoundActivity::class.java)
            intent.putExtra("selected_item", bottomNavigationView.selectedItemId)
            startActivity(intent)
        }

        // Weekly Summary
        val weeklySummaryView = findViewById<View>(R.id.weekly_summary)
        weeklySummaryView.findViewById<TextView>(R.id.item_title).text = "Weekly Summary"
        weeklySummaryView.findViewById<TextView>(R.id.item_value).text = "Friday at 5:00 PM"
        weeklySummaryView.setOnClickListener {
            val intent = Intent(this, WeeklySummaryActivity::class.java)
            intent.putExtra("selected_item", bottomNavigationView.selectedItemId)
            startActivity(intent)
        }

        // Snooze Notifications
        val snoozeView = findViewById<View>(R.id.snooze_notifications)
        snoozeView.findViewById<TextView>(R.id.item_title).text = "Snooze Notifications"
        snoozeView.findViewById<TextView>(R.id.item_subtitle).text = "Pause all for 1 hour"
        val snoozeToggle = snoozeView.findViewById<SwitchMaterial>(R.id.item_toggle)
        snoozeToggle.isChecked = sharedPreferences.getBoolean("snooze_notifications", false)
        snoozeToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("snooze_notifications", isChecked).apply()
        }
    }

    private fun setupBottomNav() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_notifications

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_tasks -> {
                    startActivity(Intent(this, MyTasksActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
