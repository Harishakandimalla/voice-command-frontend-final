package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupBottomNav()
        setupSettingsItems()
    }

    private fun setupSettingsItems() {
        // Profile Card
        findViewById<View>(R.id.profile_card_container)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // === Account Section ===
        findViewById<View>(R.id.setting_subscription)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_subscription)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "Subscription"
            it.findViewById<TextView>(R.id.tv_setting_badge)?.apply {
                visibility = View.VISIBLE
                text = "PRO"
            }
            it.setOnClickListener {
                Toast.makeText(this, "Subscription Clicked", Toast.LENGTH_SHORT).show()
            }
        }

        // === Preferences Section ===
        findViewById<View>(R.id.setting_voice)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_voice_settings)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "Voice Settings"
            it.setOnClickListener {
                startActivity(Intent(this, VoiceCommandsActivity::class.java))
            }
        }

        findViewById<View>(R.id.setting_theme)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_theme)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "Theme"
            it.setOnClickListener {
                startActivity(Intent(this, ThemeSettingsActivity::class.java))
            }
        }

        findViewById<View>(R.id.setting_notifications)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_notifications_settings)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "Notifications"
            it.setOnClickListener {
                startActivity(Intent(this, NotificationSettingsActivity::class.java))
            }
        }

        // === AI Features Section ===
        findViewById<View>(R.id.setting_ai_chatbot)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_ai_chatbot)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "AI Chatbot"
            it.findViewById<TextView>(R.id.tv_setting_badge)?.apply {
                visibility = View.VISIBLE
                text = "NEW"
            }
            it.setOnClickListener {
                startActivity(Intent(this, AiAssistantActivity::class.java))
            }
        }

        // === Support Section ===
        findViewById<View>(R.id.setting_help)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_help_support)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "Help & Support"
            it.setOnClickListener {
                startActivity(Intent(this, HelpSupportActivity::class.java))
            }
        }

        findViewById<View>(R.id.setting_about)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_about)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "About"
            it.setOnClickListener {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }

        findViewById<View>(R.id.setting_privacy)?.let {
            it.findViewById<ImageView>(R.id.iv_setting_icon)?.setImageResource(R.drawable.ic_privacy_policy)
            it.findViewById<TextView>(R.id.tv_setting_title)?.text = "Privacy Policy"
            it.setOnClickListener {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java))
            }
        }

        // === Log Out ===
        findViewById<View>(R.id.setting_logout)?.setOnClickListener {
            // TODO: Implement actual session clearing logic
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_settings

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
                R.id.navigation_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
