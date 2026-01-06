package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OnboardingFinalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_final)

        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            // ✅ Mark Onboarding Completed
            com.example.voicecommandaiapp.utils.SessionManager(this).setOnboardingCompleted()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
