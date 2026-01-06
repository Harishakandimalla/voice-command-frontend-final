package com.example.voicecommandaiapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationSoundActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var soundsRecyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    private val sounds = listOf("Default", "Chime", "Bell", "Ding", "Tone", "Breeze", "Ripple", "Echo", "Pulse")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_sound)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notification Sound"

        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val selectedSound = sharedPreferences.getString("notification_sound", "Default") ?: "Default"

        soundsRecyclerView = findViewById(R.id.sounds_recycler_view)
        soundsRecyclerView.adapter = SoundOptionsAdapter(sounds, selectedSound) { sound ->
            sharedPreferences.edit().putString("notification_sound", sound).apply()
        }

        setupBottomNav()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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