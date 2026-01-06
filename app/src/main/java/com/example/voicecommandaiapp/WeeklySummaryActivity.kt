package com.example.voicecommandaiapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Calendar

class WeeklySummaryActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dayChipGroup: ChipGroup
    private lateinit var timePicker: TimePicker

    private val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_summary)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Weekly Summary"

        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)

        dayChipGroup = findViewById(R.id.day_chip_group)
        timePicker = findViewById(R.id.time_picker)

        val selectedDay = sharedPreferences.getString("weekly_summary_day", "Friday")
        val selectedHour = sharedPreferences.getInt("weekly_summary_hour", 17)
        val selectedMinute = sharedPreferences.getInt("weekly_summary_minute", 0)

        for (i in 0 until dayChipGroup.childCount) {
            val chip = dayChipGroup.getChildAt(i) as Chip
            if (chip.text == selectedDay) {
                chip.isChecked = true
                break
            }
        }

        timePicker.hour = selectedHour
        timePicker.minute = selectedMinute

        setupBottomNav()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            saveSettings()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSettings() {
        val selectedChipId = dayChipGroup.checkedChipId
        val selectedDay = findViewById<Chip>(selectedChipId).text.toString()
        val selectedHour = timePicker.hour
        val selectedMinute = timePicker.minute

        sharedPreferences.edit()
            .putString("weekly_summary_day", selectedDay)
            .putInt("weekly_summary_hour", selectedHour)
            .putInt("weekly_summary_minute", selectedMinute)
            .apply()

        scheduleWeeklySummary()
    }

    private fun scheduleWeeklySummary() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, WeeklySummaryReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, days.indexOf(sharedPreferences.getString("weekly_summary_day", "Friday")) + 1)
        calendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("weekly_summary_hour", 17))
        calendar.set(Calendar.MINUTE, sharedPreferences.getInt("weekly_summary_minute", 0))
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    private fun setupBottomNav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // This is to retain the selected item state across activities
        bottomNavigationView.menu.findItem(intent.getIntExtra("selected_item", R.id.navigation_home)).isChecked = true


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
