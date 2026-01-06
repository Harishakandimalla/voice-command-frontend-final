package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

open class BaseOnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun setupSkip(skipViewId: Int) {
        val skip = findViewById<TextView>(skipViewId)
        skip.setOnClickListener {
            // ✅ Mark Onboarding Completed
            com.example.voicecommandaiapp.utils.SessionManager(this).setOnboardingCompleted()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
